import { Routes } from '@angular/router';

import{AppShell}from './layout/app-shell/app-shell';
import{Groups}from './pages/groups/groups';
import{TaskDashboard}from './pages/task-dashboard/task-dashboard';

export const routes:Routes=[
    {
        path: '',
        component: AppShell,
        children: [
            {
                path: '',
                redirectTo: 'groups',
                pathMatch: 'full'
            },
            {
                path: 'groups',
                component: Groups
            },
            {
                path: 'groups/:groupId/tasks',
                component: TaskDashboard
            }
        ]
    },
    {
        path: '**',
        redirectTo: 'groups'
    }
];