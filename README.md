## Jingle 


Project developed in the Design Patterns Course in ISEL
Developed this project with [Rui](https://github.com/RuiPacas) during my course.

Web application that uses [VertX](https://vertx.io/docs/vertx-web/java/) with asynchronous handlers

The application makes available the following pages : 
1. List artists with a specific name, received by query-string.
Each artist has 2 links : one for his albums, and another for his songs.
2. List an artist albums. Each album has a link for the the album songs.
3. List all songs by an artist.
4. List all the songs of an album.

The previous pages are accessible  by the following paths : 

1.	`/artists?name=...`
2.	`/artists/:id/albums`
3.	`/artists/:id/tracks`
4.	`/albums/:id/tracks`

Data is obtained from a RESTful API : https://www.last.fm/api.

App runs in the port 8080.