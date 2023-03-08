create schema dbo;

create table dbo.USER_BT(
    id int generated always as identity primary key,
    username VARCHAR(64) unique not null,
    email VARCHAR(64) unique not null,
    passwordValidation VARCHAR(256) not null,
    createDate timestamp not null,
    updateDate timestamp
);

create table dbo.TOKEN_BT(
    id int generated always as identity primary key,
    userId int not null references dbo.USER_BT(id),
    tokenValidation VARCHAR(256),
    isValid BOOL not null,
    createDate timestamp not null,
    invalidateDate timestamp,
    UNIQUE(userId,tokenValidation)
);

create table dbo.RECOVERY_TOKEN_BT(
     id int generated always as identity primary key,
     userId int not null references dbo.USER_BT(id),
     tokenRecovery VARCHAR(256),
     expireDate timestamp not null,
     createDate timestamp not null,
     isUsed BOOL not null,
     UNIQUE(userId,tokenRecovery)
);

create table dbo.STATUS_BT(
     id int generated always as identity primary key,
     userId int unique not null references dbo.USER_BT(id),
     playedGames int,
     winGames int,
     rankPoints int,
     createDate timestamp not null,
     updateDate timestamp
);

create table dbo.GAME_PHASE_BT(
     id int primary key,
     name VARCHAR(32)
);

create table dbo.GAME_BT(
    id int generated always as identity primary key,
    playerOne int not null references dbo.USER_BT(id),
    playerTwo int not null references dbo.USER_BT(id),
    phaseId int references dbo.GAME_PHASE_BT(id),
    activeRoundUserId int references dbo.USER_BT(id),
    boardPlayerOne jsonb not null,
    boardPlayerTwo jsonb not null,
    startDate timestamp,
    updateDate timestamp,
    maxShootsRule int not null,
    shootsPerRoundCount int not null,
    roundNumber int not null,
    maxTimePerRound int not null,
    roundDeadline timestamp null
);

create table dbo.LOBBY_ROOM_BT(
     id int generated always as identity primary key,
     userId int unique not null references dbo.USER_BT(id),
     shotsRule int not null,
     entryDate timestamp not null
);