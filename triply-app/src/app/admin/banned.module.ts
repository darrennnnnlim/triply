import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BannedComponent } from './banned.component';

const routes: Routes = [
  { path: '', component: BannedComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
})
export class BannedModule {}
