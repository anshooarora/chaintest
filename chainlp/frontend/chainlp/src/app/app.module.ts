import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';
import { HttpClientModule, provideHttpClient, withFetch } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { TimeagoModule } from 'ngx-timeago';
import { NgbModule, NgbOffcanvasModule, NgbTooltipModule, NgbPopoverModule, NgbDropdownModule, NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { BaseChartDirective, provideCharts, withDefaultRegisterables } from 'ng2-charts';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BuildListingComponent } from './pages/build/build-listing/build-listing.component';
import { FooterComponent } from './components/footer/footer.component';
import { HeaderComponent } from './components/header/header.component';
import { ProjectListingComponent } from './pages/project/project-listing/project-listing.component';
import { TestListingComponent } from './pages/test/test-listing/test-listing.component';
import { PaginateComponent } from './components/paginate/paginate.component';
import { BuildComponent } from './pages/build/build/build.component';
import { PrettyTimePipe } from './pipes/pretty-time.pipe';
import { FeatureComponent } from './components/feature/feature.component';
import { TestComponent } from './components/test/test.component';
import { BuildsTrendComponent } from './components/builds-trend/builds-trend.component';
import { BuildsDurationComponent } from './components/builds-duration/builds-duration.component';
import { TagListingComponent } from './pages/tag/tag-listing/tag-listing.component';
import { SettingsComponent } from './pages/settings/settings.component';
import { ExecutionsTrendComponent } from './components/executions-trend/executions-trend.component';
import { ProjectMetricsComponent } from './pages/project/project-metrics/project-metrics.component';
import { BuildTestGrowthComponent } from './components/build-test-growth/build-test-growth.component';

@NgModule({
  declarations: [
    AppComponent,
    BuildListingComponent,
    FooterComponent,
    HeaderComponent,
    ProjectListingComponent,
    TestListingComponent,
    PaginateComponent,
    BuildComponent,
    PrettyTimePipe,
    FeatureComponent,
    TestComponent,
    BuildsTrendComponent,
    BuildsDurationComponent,
    TagListingComponent,
    SettingsComponent,
    ExecutionsTrendComponent,
    ProjectMetricsComponent,
    BuildTestGrowthComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    NgbModule,
    TimeagoModule.forRoot(),
    BaseChartDirective
  ],
  providers: [
    provideClientHydration(),
    provideHttpClient(withFetch()),
    provideCharts(withDefaultRegisterables())
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
