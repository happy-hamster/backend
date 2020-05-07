alter table address
	alter column country type varchar(10),
	alter column city type varchar(60),
	alter column postcode type varchar(10),
	alter column street type varchar(75),
	alter column housenumber type varchar(50);
	
alter table location
	alter column name type varchar(100);
	
alter table occupancy
	alter column client_type type varchar(10);

alter table location_details
	alter column type type varchar(50),
	alter column brand type varchar(50);