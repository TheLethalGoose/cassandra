CREATE TABLE IF NOT EXISTS stackoverflow.latest_questions
(
    yymmdd     text,

    idQuestion uuid,
    createdAt  timestamp,
    modifiedAt  timestamp,
    idUser      uuid,
    title      text,

    votes      int,
    answers    int,
    isAnswered boolean,
    views      int,

    tags       set<text>,

    PRIMARY KEY ((yymmdd), createdAt, idQuestion)
) WITH CLUSTERING ORDER BY (createdAt DESC, idQuestion ASC);

CREATE TABLE IF NOT EXISTS stackoverflow.questions
(
    idQuestion       uuid,

    createdAt        timestamp,
    modifiedAt        timestamp,
    title            text,
    content          text,
    views            int,

    idUser           uuid,
    votes            int,
    answers          int,
    tags             set<text>,
    linkedQuestions  set<uuid>,
    relatedQuestions set<uuid>,

    PRIMARY KEY ((idQuestion))
);

CREATE TABLE IF NOT EXISTS stackoverflow.questions_by_tag
(
    tagName        text,

    idTag          uuid,
    tagInfo        text,
    tagRelatedTags set<text>,
    tagSynonyms    set<text>,


    idQuestion     uuid,
    createdAt      timestamp,
    modifiedAt      timestamp,


    isAnswered     boolean,
    idUser         uuid,
    title          text,
    views          int,

    tags           set<text>,
    votes          int,
    answers        int,

    PRIMARY KEY ((tagName), createdAt, idQuestion)
) WITH CLUSTERING ORDER BY (createdAt DESC, idQuestion ASC);

CREATE TABLE IF NOT EXISTS stackoverflow.questions_by_user
(
    idUser         uuid,

    idQuestion     uuid,
    createdAt      timestamp,
    modifiedAt      timestamp,
    title          text,
    isAnswered     boolean,
    tags set<text>,

    views          int,
    votes          int,
    answers        int,

    PRIMARY KEY ((idUser), createdAt, idQuestion)
) WITH CLUSTERING ORDER BY (createdAt DESC, idQuestion ASC);

CREATE TABLE IF NOT EXISTS stackoverflow.answers_by_question
(
    idQuestion uuid,

    idAnswer   uuid,
    createdAt  timestamp,
    modifiedAt  timestamp,
    accepted   boolean,
    idUser     uuid,
    content    text,

    votes      int,

    PRIMARY KEY ((idQuestion), idAnswer, createdAt, accepted)
) WITH CLUSTERING ORDER BY (idAnswer ASC, createdAt DESC, accepted DESC);

CREATE TABLE IF NOT EXISTS stackoverflow.user_by_email
(
    email    text,

    password text,
    idUser   uuid,

    PRIMARY KEY ((email))
);

CREATE TABLE IF NOT EXISTS stackoverflow.user
(
    idUser     uuid,

    username   text,
    email      text,
    reputation int,

    PRIMARY KEY ((idUser))
);