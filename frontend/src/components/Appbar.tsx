import React, { useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { AppBar, Avatar, Menu, MenuItem, Stack, Typography, Link, Box, Tooltip, IconButton, ListItemIcon } from '@mui/material';

import { deepOrange } from '@mui/material/colors';
import DashboardOutlinedIcon from '@mui/icons-material/DashboardOutlined';
import PersonOutlineIcon from '@mui/icons-material/PersonOutline';
import ShoppingCartOutlinedIcon from '@mui/icons-material/ShoppingCartOutlined';
import LogoutOutlinedIcon from '@mui/icons-material/LogoutOutlined';

function Appbar() {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);
  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };
  const handleClose = () => {
    setAnchorEl(null);
  };

  return (
    <AppBar sx={{ p: '5px 20px' }} position="static">
      <Stack direction="row" justifyContent="space-between" alignItems="center">
        <Typography sx={{ fontWeight: 'bold' }} variant="h5">
          슈즈랜드
        </Typography>
        <Box sx={{ display: 'flex', alignItems: 'center', textAlign: 'center' }}>
          <Tooltip title="유저정보">
            <IconButton onClick={handleClick} size="small" sx={{ ml: 2 }}>
              <Avatar sx={{ bgcolor: deepOrange[500] }} alt="Remy Sharp" src="/broken-image.jpg" />
            </IconButton>
          </Tooltip>
        </Box>
        <Menu
          anchorEl={anchorEl}
          open={open}
          onClose={handleClose}
          onClick={handleClose}
          transformOrigin={{ horizontal: 'right', vertical: 'top' }}
          anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
        >
          <Link component={RouterLink} to="/dashboard">
            <MenuItem divider>
              <ListItemIcon>
                <DashboardOutlinedIcon fontSize="small" />
              </ListItemIcon>
              대쉬보드
            </MenuItem>
          </Link>

          <Link component={RouterLink} to="/my-info">
            <MenuItem divider>
              <ListItemIcon>
                <PersonOutlineIcon fontSize="small" />
              </ListItemIcon>
              내 정보
            </MenuItem>
          </Link>

          <Link component={RouterLink} to="/cart">
            <MenuItem divider>
              <ListItemIcon>
                <ShoppingCartOutlinedIcon fontSize="small" />
              </ListItemIcon>
              장바구니
            </MenuItem>
          </Link>

          <MenuItem divider>
            <ListItemIcon>
              <LogoutOutlinedIcon fontSize="small" />
            </ListItemIcon>
            로그아웃
          </MenuItem>
        </Menu>
      </Stack>
    </AppBar>
  );
}

export default Appbar;
