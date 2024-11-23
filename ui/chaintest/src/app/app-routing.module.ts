import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BuildListingComponent } from './pages/build/build-listing/build-listing.component';
import { ProjectListingComponent } from './pages/project/project-listing/project-listing.component';
import { TestListingComponent } from './pages/test/test-listing/test-listing.component';
import { BuildComponent } from './pages/build/build/build.component';

const routes: Routes = [
  { path: '', component: ProjectListingComponent },
  { path: 'projects', component: ProjectListingComponent },
  { path: 'projects/:projectId/builds', component: BuildListingComponent },
  { path: 'projects/:projectId/builds/:buildId', component: BuildComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
