import React, { useEffect, useState } from 'react'
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import MenuIcon from '@mui/icons-material/Menu';
import { createTheme, CssBaseline, Divider, Drawer, ListItem, ListItemButton, ListItemIcon, ListItemText, ThemeProvider } from '@mui/material';
import { useAuth } from '../auth/auth.context';
import WorkspaceSwitcher from './WorkspaceSwitcher/WorkspaceSwitcher';
import WorkspaceCreateDialog from './WorkspaceCreateDialog';
import WorkspaceAlert from './CustomAlert';
import CreateTableDialog, { type CreateTableDialogProps, type TableColumn } from '../Tables/CreateTableDialog';
import { useWorkspaceContext } from '../contexts/workspace/workspace.context';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import CustomAlert from './CustomAlert';
import { DrawerHeader } from './Utility/DrawerHeader';
import TableViewIcon from '@mui/icons-material/TableView';
import { useNavigate } from 'react-router-dom';

export default function ButtonAppBar() {

  const navigate = useNavigate();

  const { logout, token } = useAuth();
  const { currentWorkspaceId, allWorkspaces } = useWorkspaceContext();

  type AlertState = {
    severity: "success" | "error"
    message: string
  };

  const [alertState, setAlertState] = useState<AlertState | null>(null);
  const [visibleAlert, setVisibleAlert] = useState<AlertState | null>(null);

  const [drawerOpen, setDrawerOpen] = useState<boolean>(false);
  const drawerWidth = 240;

  const handleDrawerOpen = () => {
    setDrawerOpen(true);
  }

  const handleDrawerClose = () => {
    setDrawerOpen(false);
  }
  

  const [openCreateTableDialog, setOpenCreateTableDialog] = useState<boolean>(false);

  const submitCreateTableRequest = async(payload: { 
          name: string,
          description: string, 
          columns: TableColumn[];
      }) => {

    if (!currentWorkspaceId) {
      console.log("Must select a workspace to create table")
    } else {

      const response = await fetch("http://172.30.107.109:8080/api/tables/create", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "X-Workspace-Id": `${currentWorkspaceId}`,
          "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
      })

      if (!response.ok) {
        setAlertState({
          severity: "error", 
          message: "Failed to create table!"
        })
        console.log(response.statusText)
      } else {

        setAlertState({ 
          severity: "success",
          message: `Successfully created table ${payload.name} in workspace ${allWorkspaces.byId[currentWorkspaceId].name}!`
        })

        // Console logging
        const responseData = await response.json()
        console.log(responseData)
      }
    }
    setOpenCreateTableDialog(false)
  }

  // visible status lags behind alert state when alert state is set to null - for stable status during exit animation. 
  useEffect(() => {
    if (alertState !== null) {
      setVisibleAlert(alertState);
    }
  }, [alertState])

  return (
    <Box sx={{ flexGrow: 1 }}>
      {/* color="primary" */}
      {/* sx={{ backgroundColor: 'primary.main', }} */}

      <AppBar position="static"> 
        <Toolbar>
          <IconButton
            size="large"
            edge="start"
            color="inherit"
            aria-label="open-drawer"
            onClick={handleDrawerOpen}
            sx={{ mr: 2 }}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            News
          </Typography>

          <Button variant="contained" onClick={() => setOpenCreateTableDialog(true)} size="medium" sx={{ height: 40 }}>
              Create Table
          </Button>
          <CreateTableDialog
            open={openCreateTableDialog}
            handleClose={() => setOpenCreateTableDialog(false)}
            handleSubmit={submitCreateTableRequest}
          ></CreateTableDialog>

          <WorkspaceCreateDialog 
            onSuccess={() => { 
              setAlertState({ 
                severity: "success",
                message: "Workspace created successfully!"
              })
            }} 
            onError={() => { 
              setAlertState({ 
                severity: "error",
                message: "Failed to create workspace!"
              })
            }}
          />

          <WorkspaceSwitcher/>

          <Button color="inherit" onClick={logout} sx={{ border: '1px solid' }}>Logout</Button>
        </Toolbar>

      </AppBar>

      <Drawer
          sx={{ 
            width: drawerWidth, 
            '& .MuiDrawer-paper': {
              width: drawerWidth,
              boxSizing: 'border-box'
            }
          }}
          open={drawerOpen}
          variant='persistent'
        >
        <DrawerHeader>
          <IconButton onClick={handleDrawerClose}>
            <ChevronLeftIcon />
          </IconButton>
        </DrawerHeader>
        <Divider></Divider>
        <ListItem key={"table-ui"}>
          <ListItemButton onClick={() => navigate("/tables")}>
            <ListItemIcon>
              <TableViewIcon/>
            </ListItemIcon>
            <ListItemText primary={"Tables"} secondary={"View & mutate"}/>
          </ListItemButton>
        </ListItem>
      


      </Drawer>

      <CustomAlert 
        open={alertState !== null} 
        severity={visibleAlert?.severity ?? "success"}
        message={visibleAlert?.message ?? ""}
        handleClose={() => setAlertState(null)}></CustomAlert>
    </Box>
  );
}

