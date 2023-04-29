CREATE TABLE IF NOT EXISTS stackoverflow.latest_questions
(
    yymmdd     text,

    idQuestion uuid,
    createdAt  timestamp,
    createdBy  uuid STATIC,

    title      text,
    views      int,
    votes      int,
    answers    int,
    isAnswered boolean,
    tags       set<text>,

    PRIMARY KEY ((yymmdd), createdAt, idQuestion)
    ) WITH CLUSTERING ORDER BY (createdAt DESC, idQuestion ASC);

CREATE TABLE IF NOT EXISTS stackoverflow.question
(
    idQuestion       uuid,

    createdAt        timestamp,
    lastModifiedAt   timestamp,

    createdBy          uuid,
    title            text,
    content          text,

    votes            int,
    views            int,

    tags             set<text>,
    linkedQuestions  set<uuid>,
    relatedQuestions set<uuid>,

    PRIMARY KEY ((idQuestion))
);

CREATE TABLE IF NOT EXISTS stackoverflow.questions_by_tag
(
    tagName            text,

    idQuestion         uuid,
    questionCreatedAt  timestamp,

    idTag              uuid STATIC,
    tagInfo            text,
    tagRelatedTags     text,
    tagSynonyms        text,

    questionCreatedBy  uuid STATIC,
    questionTitle      text,
    questionViews      int,
    questionVotes      int,
    questionAnswers    int,
    questionIsAnswered boolean,

    PRIMARY KEY ((tagName), questionCreatedAt, idQuestion)
    ) WITH CLUSTERING ORDER BY (questionCreatedAt DESC, idQuestion ASC);

CREATE TABLE IF NOT EXISTS stackoverflow.answers_by_question
(
    idParent  uuid,

    idAnswer  uuid,
    createdAt timestamp,
    accepted  boolean,

    creator   uuid STATIC,
    content   text,
    votes     int,

    PRIMARY KEY ((idParent), idAnswer, createdAt, accepted)
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