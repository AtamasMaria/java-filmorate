delete from FRIENDS;
delete  from FILM_GENRES;
delete from LIKES;
delete  from USERS;
delete  from FILMS;
delete  from GENRES;
delete  from MPA;

insert into GENRES(GENRE_ID, NAME) VALUES ( '1','Комедия' );
insert into GENRES(GENRE_ID, NAME) VALUES ( '2','Драма' );
insert into GENRES(GENRE_ID, NAME) VALUES ( '3','Мультфильм' );
insert into GENRES(GENRE_ID, NAME) VALUES ( '4','Триллер' );
insert into GENRES(GENRE_ID, NAME) VALUES ( '5','Документальный' );
insert into GENRES(GENRE_ID, NAME) VALUES ( '6','Боевик' );

insert into MPA(MPA_ID, NAME, DESCRIPTION) VALUES ( '1','G', 'Нет возрастных ограничений');
insert into MPA(MPA_ID, NAME, DESCRIPTION) VALUES ( '2','PG', 'Рекомендуется присутствие родителей' );
insert into MPA(MPA_ID, NAME, DESCRIPTION) VALUES ( '3','PG-13', 'Детям до 13 лет просмотр не желателен' );
insert into MPA(MPA_ID, NAME, DESCRIPTION) VALUES ( '4','R', 'Лицам до 17 лет обязательно присутствие взрослого' );
insert into MPA(MPA_ID, NAME, DESCRIPTION) VALUES ( '5','NC-17', 'Лицам до 18 лет просмотр запрещен' );


