import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BuildListingComponent } from './pages/build/build-listing/build-listing.component';
import { ProjectListingComponent } from './pages/project/project-listing/project-listing.component';
import { BuildComponent } from './pages/build/build/build.component';
import { TagListingComponent } from './pages/tag/tag-listing/tag-listing.component';
import { SettingsComponent } from './pages/settings/settings.component';
import { ProjectMetricsComponent } from './pages/project/project-metrics/project-metrics.component';

const routes: Routes = [
  { path: '', component: ProjectListingComponent },
  { path: 'projects', component: ProjectListingComponent },
  { path: 'projects/:projectId/builds', component: BuildListingComponent },
  { path: 'projects/:projectId/tags', component: TagListingComponent },
  { path: 'projects/:projectId/metrics', component: ProjectMetricsComponent },
  { path: 'projects/:projectId/builds/:buildId', component: BuildComponent },
  { path: 'builds/:buildId', component: BuildComponent },
  { path: 'settings', component: SettingsComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
