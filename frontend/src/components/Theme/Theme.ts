import { blue, cyan } from "@mui/material/colors"; 
import { createTheme } from "@mui/material";

export const orcaTheme =  createTheme({
    palette: {
        primary: { main: '#0066CC' },
        secondary: { main: '#00BCD4'}
    },
    components: {
        MuiAppBar: {
            styleOverrides: {
                root: ({ theme }) => ({
                    backgroundImage: 
                        `linear-gradient(90deg, ${theme.palette.primary.main} 0%, ${theme.palette.secondary.main} 100%)`,
                        color: theme.palette.common.white,
                }),
            },
        },
    },
    mixins: {
        toolbar: {
            minHeight: 60,
        }
    }
});