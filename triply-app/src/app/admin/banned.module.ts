import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BannedComponent } from './banned.component';
import { FormsModule } from '@angular/forms';

const routes: Routes = [{ path: '', component: BannedComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes), FormsModule],
})
export class BannedModule {}
