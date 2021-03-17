CREATE INDEX customer_id
ON Customer
USING BTREE
(id);

CREATE INDEX reservation_cruise_id
ON Reservation
USING BTREE
(cid);

CREATE INDEX reservation_customer_id
ON Reservation
USING BTREE
(ccid);

CREATE INDEX reservation_status
ON Reservation
USING BTREE
(status);
