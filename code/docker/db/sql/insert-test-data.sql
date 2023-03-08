set schema 'dbo';

-- test users
insert into dbo.USER_BT (username,email,passwordValidation,createDate) VALUES('tduarte','a42525@alunos.isel.pt','123456',Current_timestamp);
insert into dbo.USER_BT (username,email,passwordValidation,createDate) VALUES('rsantos','a44806@alunos.isel.pt','123456',Current_timestamp);

-- gamePhase
insert into dbo.GAME_PHASE_BT(id,name) VALUES(1,'LAYOUT');
insert into dbo.GAME_PHASE_BT(id,name) VALUES(2,'SHOOTING_PLAYER_ONE');
insert into dbo.GAME_PHASE_BT(id,name) VALUES(3,'SHOOTING_PLAYER_TWO');
insert into dbo.GAME_PHASE_BT(id,name) VALUES(4,'PLAYER_ONE_WON');
insert into dbo.GAME_PHASE_BT(id,name) VALUES(5,'PLAYER_TWO_WON');