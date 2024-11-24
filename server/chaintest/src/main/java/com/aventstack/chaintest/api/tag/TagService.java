package com.aventstack.chaintest.api.tag;

import com.aventstack.chaintest.api.domain.Taggable;
import com.aventstack.chaintest.api.test.Test;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@Transactional(readOnly = true)
public class TagService {

    private static final Object lock = new Object();
    private static final ConcurrentHashMap<String, Tag> CACHE = new ConcurrentHashMap<>();

    @Autowired
    private TagRepository repository;

    @Cacheable(value = "tags", unless = "#result == null || #result.size == 0")
    public Page<Tag> findAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Cacheable(value = "tag", key = "#id")
    public Tag findById(final long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TagNotFoundException("Not found"));
    }

    public Optional<Tag> findByName(final String tagName) {
        if (!CACHE.containsKey(tagName)) {
            final Optional<Tag> tag = repository.findByName(tagName);
            tag.ifPresent(t -> CACHE.put(tagName, t));
            return tag;
        }
        return Optional.of(CACHE.get(tagName));
    }

    @Transactional
    @CacheEvict(value = "tags", allEntries = true)
    @CachePut(value = "tag", key = "#tag.id")
    public Tag create(final Tag tag) {
        final Optional<Tag> found = findByName(tag.getName());
        return found.orElseGet(() -> {
            log.debug("Saving tag: " + tag);
            synchronized (lock) {
                if (CACHE.containsKey(tag.getName())) {
                    log.debug("Tag " + tag + " exists, returning value from cache");
                    return CACHE.get(tag.getName());
                }
                final Tag saved = repository.saveAndFlush(tag);
                log.debug("Saved tag " + tag);
                CACHE.put(saved.getName(), saved);
                return saved;
            }
        });
    }

    @Transactional
    @CacheEvict(value = "tags", allEntries = true)
    @CachePut(value = "tag", key = "#tag.id")
    public Tag update(final Tag tag) {
        log.info("Saving b " + tag);
        repository.findById(tag.getId()).ifPresentOrElse(
                x -> repository.save(tag),
                () -> { throw new TagNotFoundException("Tag with ID " + tag.getId() + " was not found"); }
        );
        return tag;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "tags", allEntries = true, condition = "#id > 0"),
            @CacheEvict(value = "tag", key = "#id", condition="#id > 0")
    })
    public void delete(final long id) {
        log.info("Deleting tag with id " + id);
        repository.deleteById(id);
        log.info("Tag id: " + id + " was deleted successfully");
    }

    public void createAssignTags(final Taggable taggable) {
        if (null != taggable.getTags() && !taggable.getTags().isEmpty()) {
            final Set<Tag> tags = taggable.getTags();
            for (final Tag tag : tags) {
                final Tag created = findByName(tag.getName()).orElse(create(tag));
                tag.setId(created.getId());
            }
            if (taggable instanceof Test) {
                final Collection<Test> nodes = ((Test) taggable).getChildren();
                if (null != nodes) {
                    for (final Test test : nodes) {
                        createAssignTags(test);
                    }
                }
            }
        }
    }

}
