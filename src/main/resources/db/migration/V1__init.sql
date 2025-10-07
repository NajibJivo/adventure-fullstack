-- ==== Core tables ====

-- Activity
CREATE TABLE IF NOT EXISTS activity (
    id    BIGINT PRIMARY  KEY AUTO_INCREMENT,
    name            VARCHAR(200)    NOT NULL,
    description     TEXT            NOT NULL,
    price           DECIMAL(10,2)   NOT NULL,
    duration        SMALLINT        NOT NULL,       -- minutter
    min_age         TINYINT         NOT NULL,       -- alderskrav
    min_height      SMALLINT        NOT NULL,       -- højdekrav i cm
    available_from  DATETIME        NULL,
    available_to    DATETIME        NULL,
    image_url       VARCHAR(255)    NULL
);

-- CUSTOMER
CREATE TABLE IF NOT EXISTS customer (
    id    BIGINT PRIMARY  KEY AUTO_INCREMENT,
    name            VARCHAR(120)    NOT NULL,
    phone           VARCHAR(100)    NULL,
    email           VARCHAR(120)    NOT NULL UNIQUE,
    user_role  ENUM('CUSTOMER', 'EMPLOYEE', 'OWNER') NOT NULL
);

-- Equipment
CREATE TABLE IF NOT EXISTS equipment (
    id    BIGINT PRIMARY KEY AUTO_INCREMENT,
    equipment_name   VARCHAR(100)    NOT NULL,
    maintenance_date DATE            NULL
);

-- ActivityEquipment (M:N)
CREATE TABLE IF NOT EXISTS activity_equipment (
    activity_id     BIGINT          NOT NULL,
    equipment_id    BIGINT          NOT NULL,
    PRIMARY KEY (activity_id, equipment_id),
    CONSTRAINT fk_activity_equipment_activity
        FOREIGN KEY (activity_id) REFERENCES activity(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_activity_equipment_equipment
        FOREIGN KEY (equipment_id) REFERENCES equipment(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Booking
CREATE TABLE IF NOT EXISTS booking(
    id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id     BIGINT           NOT NULL,
    customer_id     BIGINT           NOT NULL,
    start_datetime  DATETIME         NOT NULL,
    participants    INT              NOT NULL,
    booking_status ENUM('EDIT', 'CANCELLED') NOT NULL,
    instructor_name VARCHAR(100),

    CONSTRAINT fk_booking_activity
        FOREIGN KEY (activity_id)  REFERENCES activity(id)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    CONSTRAINT fk_booking_customer
        FOREIGN KEY (customer_id) REFERENCES customer(id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

-- INDEX + UNIQUE jf. ERD
CREATE INDEX idx_booking_activity_time
    ON booking(activity_id, start_datetime);

CREATE UNIQUE INDEX uq_booking_activity_customer_time
    ON booking(activity_id, customer_id, start_datetime);


-- ===== DEL2: arrangement, product/sales, roster =====

-- Arrangement (firmaevent)
CREATE TABLE IF NOT EXISTS arrangement(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id  BIGINT        NOT NULL,
    title        VARCHAR(200)  NOT NULL,
    event_date   DATE          NOT NULL,
    notes        TEXT          NULL,
    CONSTRAINT fk_arrangement_customer
    FOREIGN KEY (customer_id) REFERENCES customer(id)
    ON DELETE RESTRICT ON UPDATE CASCADE
    );

CREATE INDEX idx_arrangement_customer
    ON arrangement(customer_id, event_date);

-- ArrangementActivity (M:N mellem arrangement og activity)
CREATE TABLE IF NOT EXISTS arrangement_activity(
    arrangement_id BIGINT NOT NULL,
    activity_id    BIGINT NOT NULL,
    PRIMARY KEY (arrangement_id, activity_id),
    CONSTRAINT fk_arr_act_arr
    FOREIGN KEY (arrangement_id) REFERENCES arrangement(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_arr_act_act
    FOREIGN KEY (activity_id) REFERENCES activity(id)
    ON DELETE RESTRICT ON UPDATE CASCADE
    );

-- Product (kioskvarer)
CREATE TABLE IF NOT EXISTS product(
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    name      VARCHAR(150)   NOT NULL,
    price     DECIMAL(10,2)  NOT NULL,
    is_active BOOLEAN        NOT NULL DEFAULT TRUE
    );

CREATE UNIQUE INDEX uq_product_name
    ON product(name);

-- Sale (et salg i kiosken) --
CREATE TABLE IF NOT EXISTS sale(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    sale_datetime DATETIME   NOT NULL,
    customer_id   BIGINT     NULL,   -- kan være NULL ved kontantsalg
    CONSTRAINT    fk_sale_customer
    FOREIGN KEY   (customer_id) REFERENCES customer(id)
    ON DELETE SET NULL ON UPDATE CASCADE
    );

CREATE INDEX idx_sale_datetime
    ON sale(sale_datetime);

-- SaleLine (linjer pr. salg; historisk unit_price bevares)
CREATE TABLE IF NOT EXISTS sale_line(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    sale_id     BIGINT        NOT NULL,
    product_id  BIGINT        NOT NULL,
    quantity    INT           NOT NULL,
    unit_price  DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_saleline_sale
    FOREIGN KEY (sale_id) REFERENCES sale(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_saleline_product
    FOREIGN KEY (product_id) REFERENCES product(id)
    ON DELETE RESTRICT ON UPDATE CASCADE
    );

CREATE INDEX idx_saleline_sale
    ON sale_line(sale_id);

-- Roster (vagtplan pr. medarbejder (customer.role = 'EMPLOYEE'))
CREATE TABLE IF NOT EXISTS roster(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id  BIGINT       NOT NULL,  -- FK til customer (rolle = EMPLOYEE)
    work_date    DATE         NOT NULL,
    note         VARCHAR(200) NULL,
    CONSTRAINT fk_roster_employee
    FOREIGN KEY (employee_id) REFERENCES customer(id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uq_roster_employee_day
    UNIQUE (employee_id, work_date)
    );