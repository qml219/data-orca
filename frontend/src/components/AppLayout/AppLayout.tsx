import React, { useState } from 'react';
import TopNavBar from '@/components/TopNavBar/TopNavBar';
import Grid from '@mui/material/Grid';
import { Outlet } from 'react-router-dom';
import { Drawer } from '@mui/material';


export default function AppLayout() {

    return (
        <Grid container sx={{ width: 1 // '80%' or 0.8
        }} spacing={2} direction='column'>
            <Grid size={12}>
                 <TopNavBar></TopNavBar>
            </Grid>
            <Grid>
                <Outlet/>
            </Grid>
        </Grid>
    )
}