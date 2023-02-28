delete from FRIENDS;
delete  from FILMS_GENRE;
delete from LIKES;
delete  from USERS;
delete  from FILMS;
delete  from GENRE;
delete  from RATING;




insert into GENRE(GENRE_ID, NAME) VALUES ( '1','Комедия' );
insert into GENRE(GENRE_ID, NAME) VALUES ( '2','Драма' );
insert into GENRE(GENRE_ID, NAME) VALUES ( '3','Мультфильм' );
insert into GENRE(GENRE_ID, NAME) VALUES ( '4','Триллер' );
insert into GENRE(GENRE_ID, NAME) VALUES ( '5','Документальный' );
insert into GENRE(GENRE_ID, NAME) VALUES ( '6','Боевик' );

insert into RATING(RATING_ID, NAME) VALUES ( '1','G', 'у фильма нет возрастных ограничений');
insert into RATING(RATING_ID, NAME) VALUES ( '2','PG', 'детям рекомендуется смотреть фильм с родителями');
insert into RATING(RATING_ID, NAME) VALUES ( '3','PG-13', 'детям до 13 лет просмотр не желателен');
insert into RATING(RATING_ID, NAME) VALUES ( '4','R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого');
insert into RATING(RATING_ID, NAME) VALUES ( '5','NC-17', 'лицам до 18 лет просмотр запрещён');