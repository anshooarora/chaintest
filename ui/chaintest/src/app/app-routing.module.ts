import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BuildListingComponent } from './pages/build/build-listing/build-listing.component';
import { ProjectListingComponent } from './pages/project/project-listing/project-listing.component';

const routes: Routes = [
  { path: '', component: ProjectListingComponent },
  { path: 'projects', component: ProjectListingComponent },
  { path: 'projects/:id/builds', component: BuildListingComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
