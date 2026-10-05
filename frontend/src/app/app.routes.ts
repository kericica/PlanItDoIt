import { Routes } from '@angular/router';

import{AppShell}from './layout/app-shell/app-shell';
import{Groups}from './pages/groups/groups';
import{TaskDashboard}from './pages/task-dashboard/task-dashboard';
import{Profile}from './pages/profile/profile';

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
            },
            {
                path: 'profile',
                component: Profile
            }
        ]
    },
    {
        path: '**',
        redirectTo: 'groups'
    }
];