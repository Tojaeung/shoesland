import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { styled, Card, CardActionArea, CardContent, Container, Grid, Typography } from '@mui/material';

interface UserType {
  id: number;
  firstName: string;
  lastName: string;
  maidenName: string;
  age: number;
  gender: string;
  email: string;
  phone: string;
  username: string;
  password: string;
  birthDate: string;
  image: string;
  bloodGroup: string;
  height: number;
  weight: number;
  eyeColor: string;
}

function Home() {
  const [users, setUsers] = useState<UserType[] | undefined>();
  useEffect(() => {
    axios.get('https://dummyjson.com/users?limit=50').then((res) => setUsers(res.data.users));
  }, []);
  console.log(users);

  return (
    <Container maxWidth="xl">
      <Grid container spacing={{ xs: 1, md: 2 }}>
        {users?.map((user) => (
          <Grid item key={user.id} xs={12} sm={6} md={4} lg={2}>
            <Card>
              <CardActionArea>
                <UserImage src={user.image} alt={`${user.maidenName}의 사진`} />
                <CardContent>
                  <Typography gutterBottom variant="h5" component="div">
                    {user.email}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {user.username}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {user.phone}
                  </Typography>
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>
    </Container>
  );
}

export default Home;

const UserImage = styled('img')({
  width: '100%',
  objectFit: 'cover',
});
