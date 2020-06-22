create table content (
  id long not null auto_increment,
  title varchar(100) not null,
  description varchar(1000),
  year_of_release long,
  primary key (id)
);