import { Component } from '@angular/core';
import{RouterLink,RouterLinkActive,RouterOutlet}from '@angular/router';

@Component({
  selector: 'app-task-dashboard',
  imports: [RouterLink,RouterLinkActive,RouterOutlet],
  templateUrl: './task-dashboard.html',
  styleUrl: './task-dashboard.css'
})
export class TaskDashboard {

}
