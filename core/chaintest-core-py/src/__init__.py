import time
from enum import Enum
from typing import List, Set
import uuid
from jinja2 import Environment, PackageLoader, select_autoescape


class Tag:
    def __init__(self, name):
        self.name = name

class Test:
    def __init__(self, build_id: int = 0, name: str = "", test_class: str = None, tags: List[Tag] = None):
        self.id: int = 0
        self.build_id: int = build_id
        self.project_id: int = 0
        self.client_id: uuid.UUID = uuid.uuid4()
        self.external_id: str = None
        self.name: str = name
        self.description: str = None
        self.class_name: str = test_class
        self.started_at: int = int(time.time() * 1000)
        self.ended_at: int = 0
        self.duration_ms: int = 0
        self.result: str = "PASSED"
        self.tags: Set[Tag] = set(tags) if tags else set()
        self.error: str = None
        self.children: List[Test] = []
        self.logs: List[str] = []
        self.depth: int = 0
        self.is_bdd: bool = False
        self.parent: Test = None

    def complete(self, error: str = None):
        self.ended_at = int(time.time() * 1000)
        self.duration_ms = self.ended_at - self.started_at
        if error:
            self.error = str(error)
            self.result = "FAILED"
        if self.parent:
            self.parent.result = max(self.result, self.parent.result, key=lambda r: Result[r].priority)
            self.parent.complete()

    def add_tags(self, tags: List[Tag]):
        if tags:
            self.tags.update(tags)

    def add_tag(self, tag: Tag):
        if tag:
            self.tags.add(tag)
            if self.parent:
                self.parent.add_tag(tag)

    def add_child(self, child: 'Test'):
        child.parent = self
        child.depth = self.depth + 1
        self.children.append(child)
        for tag in child.tags:
            self.add_tag(tag)

    def add_log(self, log: str):
        self.logs.append(log)

    def set_is_bdd(self, is_bdd: bool):
        self.is_bdd = is_bdd
        for child in self.children:
            child.set_is_bdd(is_bdd)

    def duration_pretty(self):
        return TimeUtil.get_pretty_time(self.duration_ms)

class Stats:
    depth: int = 0
    total: int = 0
    passed: int = 0
    failed: int = 0
    skipped: int = 0

    def update(self, test):
        self.total += 1
        if test.result == "PASSED":
            self.passed += 1
        elif test.result == "FAILED":
            self.failed += 1
        elif test.result == "SKIPPED":
            self.skipped += 1

class BuildStats(Stats):
    def __init__(self, depth):
        self.depth = depth

    def update(self, test):
        super(BuildStats, self).update(test)

class TagStats(Stats):
    def __init__(self, name, depth = 0):
        self.depth = depth
        self.name = name
        self.duration_ms = 0

    def update(self, test):
        super(TagStats, self).update(test)
        self.duration_ms += test.duration_ms

    def duration_pretty(self):
        return TimeUtil.get_pretty_time(self.duration_ms)

class Build:
    def __init__(self, project_name="default", testrunner=None):
        self.id = None
        self.project_id = 0
        self.project_name = project_name
        self.started_at = time.time()
        self.ended_at = None
        self.duration_ms = None
        self.execution_stage = "IN_PROGRESS"
        self.testrunner = testrunner
        self.name = None
        self.result = Result.PASSED
        self.buildstats: List[BuildStats] = []
        self.tag_stats = set()
        self.tag_stats_monitor = {}
        self.tags = set()
        self.bdd = False
        self.system_info = []

    def update_stats(self, test: Test):
        self.complete(test.result)
        self.result = Result.compute_priority(self.result, test.result)
        self.update_buildstats(test)
        self.update_tagstats(test)
        test.bdd = self.bdd

    def update_buildstats(self, test):
        stat = next((x for x in self.buildstats if x.depth == test.depth), None)
        if not stat:
            stat = self.add_buildstats_depth(test.depth)
        stat.update(test)
        for t in test.children:
            self.update_buildstats(t)

    def add_buildstats_depth(self, depth):
        stat = BuildStats(depth)
        self.buildstats.append(stat)
        return stat

    def update_tagstats(self, test):
        if test.tags:
            self.add_tags(test.tags)
            for tag in test.tags:
                if tag.name not in self.tag_stats_monitor:
                    ts = TagStats(test.depth)
                    ts.name = tag.name
                    self.tag_stats.add(ts)
                    self.tag_stats_monitor[tag.name] = ts
                self.tag_stats_monitor[tag.name].update(test)

    def complete(self, result=None):
        self.ended_at = time.time()
        self.duration_ms = (self.ended_at - self.started_at) * 1000
        if result:
            self.result = result

    def get_duration_pretty(self):
        return TimeUtil.get_pretty_time(self.duration_ms)

    def add_tags(self, tags):
        if tags:
            self.tags.update(tags)

class TimeUtil:
    @staticmethod
    def get_pretty_time(millis):
        if millis < 1_000:
            return f"{millis}ms"
        if millis < 60_000:
            return f"{millis // 1_000 % 60}s"
        if millis < 3_600_000:
            minutes = millis // 60_000
            seconds = millis // 1_000 % 60
            return f"{minutes}m {seconds}s"
        hours = millis // 3_600_000
        minutes = (millis // 60_000) % 60
        seconds = millis // 1_000 % 60
        return f"{hours}h {minutes}m {seconds}s"

class Result(Enum):
    UNKNOWN = ("UNKNOWN", -1)
    PASSED = ("PASSED", 0)
    UNDEFINED = ("UNDEFINED", 10)
    SKIPPED = ("SKIPPED", 20)
    FAILED = ("FAILED", 30)

    def __init__(self, result: str, priority: int):
        self._result = result
        self._priority = priority

    @property
    def result(self) -> str:
        return self._result

    @property
    def priority(self) -> int:
        return self._priority

    @staticmethod
    def parse_result(result_string: str) -> 'Result':
        for result in Result:
            if result.result.lower() == result_string.lower():
                return result
        return Result.UNKNOWN

    @staticmethod
    def compute_priority(a: 'Result', b: 'Result') -> 'Result':
        if a == Result.FAILED or b == Result.FAILED:
            return Result.FAILED
        return Result.PASSED

    @staticmethod
    def compute_priority_from_strings(a: str, b: str) -> 'Result':
        ar = Result.parse_result(a)
        br = Result.parse_result(b)
        return Result.compute_priority(ar, br)

class ChainTestGenerator:
    def __init__(self):
        pass

    def start(self, build: Build):
        pass

    def flush(self, tests: List[Test]):
        pass

    def after_test(self, test: Test):
        pass

class ChainTestPluginService:
    def __init__(self, testrunner):
        self.build: Build = Build(project_name="python", testrunner=testrunner)
        self.tests: List[Test] = []
        self.generators: List[ChainTestGenerator] = []

    def register(self, generator: ChainTestGenerator = None):
        self.generators.append(generator)

    def start(self):
        for generator in self.generators:
            generator.start(self.build)

    def after_test(self, test: Test):
        test.complete()
        self.tests.append(test)
        self.build.update_stats(test)
        for generator in self.generators:
            generator.after_test(test)

    def flush(self):
        for generator in self.generators:
            generator.flush(self.tests)


class ChainTestSimpleGenerator(ChainTestGenerator):
    def __init__(self, config=None):
        super().__init__()
        self.build: Build = None
        self.env = Environment(
            loader=PackageLoader("simple"),
            autoescape=select_autoescape()
        )
        self.template = self.env.get_template("index.html")
        self.config = config
        if not config:
            self.config = {
                "project_name": "default",
                "theme": "dark",
                "offline" : True,
                "document_title": "default",
                "css": "",
                "js": ""
            }

    def start(self, build: Build):
        self.build = build

    def flush(self, tests: List[Test]):
        text = self.template.render(config=self.config, build=self.build, tests=tests)
        with open("out/index.html", "w") as f:
            f.write(text)

class ChainTestEmailGenerator(ChainTestGenerator):
    def __init__(self, config=None):
        super().__init__()

class ChainLPGenerator(ChainTestGenerator):
    def __init__(self, config=None):
        super().__init__()


build = Build("Python", "pytest")

service = ChainTestPluginService(testrunner="pytest")
service.register(ChainTestSimpleGenerator())
service.start()
service.after_test(Test(build.id, "One", None, [Tag("tag1"), Tag("tag2")]))
service.after_test(Test(build.id, "Two"))
service.after_test(Test(build.id, "Three", "py.test"))
service.flush()
