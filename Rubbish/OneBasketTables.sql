######## TABLES ########
## Country Data ##
Create table Country(
	countryId int primary key auto_increment not null,
    name varchar(75) unique
);

## State Data ##
Create table State (
	stateId int primary key auto_increment not null,
    name varchar(100),
    countryId int not null,
    foreign key (countryId) references Country (countryId) 
);

## Address data ##
Create table Address (
	addressId int primary key auto_increment not null,
    streetAddress varchar(255) not null,
    zipCode varchar(10),
    city varchar(255) not null,
    stateId int not null,
    foreign key (stateId) references State (stateId)
);

## Asset Data ##
## Type determines what values should be what            ##
## This allows for ease and efficiency in Data transfers ##
Create table Asset (
	assetId int primary key auto_increment not null,
    assetType char not null,
    assetCode varchar(20) not null,
    apr float default 0.0,
    label varchar(255) not null,
    quarterlyDividend double default null,
    rateOfReturn float default null,
    risk float default 0.0,
    # sharePrice double default null, :: Deprecated and merged with `value`
    symbol varchar(5) unique,
	`value` double not null default 0.0
);

## Person Data ##
Create table Person (
	personId int primary key auto_increment not null,
    personCode varchar(20) not null,
	lastName varchar(255) not null,
    firstName varchar(255),
    addressId int not null,
    foreign key (addressId) references Address (addressId)
);

## Broker Data ##
## If a person is a broker, normalized table for extra info. ##
Create table BrokerStatus (
	brokerId int primary key auto_increment not null,
    brokerType char,
    secId varchar(10) unique,
    personId int not null,
    foreign key (personId) references Person (personId)
);

## Email Data ##
Create table Email (
	emailId int primary key auto_increment not null,
    address varchar(255) not null unique
);

## Portfolio Data ##
Create table Portfolio (
	portfolioId int primary key auto_increment not null,
    ownerId int not null,
    brokerId int not null,
    beneficiaryId int,
    foreign key (ownerId) references Person (personId),
    foreign key (brokerId) references Person (personId),
    foreign key (beneficiaryId) references Person (personId),
    title varchar(100) not null unique
);

######## JOIN TABLES ########
## Joins Email to Person ##
Create table PersonEmail (
	personEmailId int primary key auto_increment not null,
    personId int not null,
    emailId int not null,
    foreign key (personId) references Person (personId),
    foreign key (emailId) references Email (emailId)
);



## Joins Asset to Portfolio ##
Create table AssetPortfolio (
	assetPortfolioId int primary key auto_increment not null,
    number double not null,
    assetId int not null,
	portfolioId int not null,
    foreign key (assetId) references Asset (assetId),
    foreign key (portfolioId) references Portfolio (portfolioId)
); 
