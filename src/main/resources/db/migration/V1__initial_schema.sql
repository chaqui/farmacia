-- =============================================================
-- V1__initial_schema.sql
-- Migración inicial para PostgreSQL
-- Generado a partir de entidades JPA del proyecto inventario
-- =============================================================

-- ---------------------------------------------------------------
-- Tablas independientes (sin foreign keys)
-- ---------------------------------------------------------------

CREATE TABLE categoria (
    id        BIGSERIAL    PRIMARY KEY,
    nombre    VARCHAR(255),
    descripcion VARCHAR(255)
);

CREATE TABLE marcas (
    id          SERIAL       PRIMARY KEY,
    nombre      VARCHAR(255) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

CREATE TABLE proveedor (
    id        BIGSERIAL    PRIMARY KEY,
    nombre    VARCHAR(255) UNIQUE,
    direccion VARCHAR(255),
    telefono  VARCHAR(255),
    email     VARCHAR(255),
    contacto  VARCHAR(255)
);

CREATE TABLE sucursal (
    id     BIGSERIAL    PRIMARY KEY,
    nombre VARCHAR(255),
    activo BOOLEAN
);

CREATE TABLE clientes (
    id             SERIAL       PRIMARY KEY,
    nombre         VARCHAR(255) NOT NULL,
    tipo_cliente   INTEGER,
    limite_credito REAL
);

-- ---------------------------------------------------------------
-- Tablas con dependencias de 1er nivel
-- ---------------------------------------------------------------

CREATE TABLE producto (
    id                   BIGSERIAL    PRIMARY KEY,
    nombre               VARCHAR(255),
    descripcion          VARCHAR(255),
    codigo               VARCHAR(255) UNIQUE,
    porcentaje_descuento REAL,
    porcentaje_ganancia  REAL,
    estanteria           VARCHAR(255),
    nivel                INTEGER,
    bodega               VARCHAR(255),
    cantidad_minima      INTEGER,
    proveedor_id         BIGINT       NOT NULL,
    marca_id             INTEGER,
    CONSTRAINT fk_producto_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedor(id),
    CONSTRAINT fk_producto_marca     FOREIGN KEY (marca_id)     REFERENCES marcas(id)
);

-- ---------------------------------------------------------------
-- Tablas join (ManyToMany)
-- ---------------------------------------------------------------

CREATE TABLE producto_categoria (
    producto_id  BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    PRIMARY KEY (producto_id, categoria_id),
    CONSTRAINT fk_pc_producto  FOREIGN KEY (producto_id)  REFERENCES producto(id),
    CONSTRAINT fk_pc_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);

CREATE TABLE proveedor_marca (
    proveedor_id BIGINT  NOT NULL,
    marca_id     INTEGER NOT NULL,
    PRIMARY KEY (proveedor_id, marca_id),
    CONSTRAINT fk_pm_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedor(id),
    CONSTRAINT fk_pm_marca     FOREIGN KEY (marca_id)     REFERENCES marcas(id)
);

CREATE TABLE producto_relacionados (
    producto_id   BIGINT NOT NULL,
    relacionado_id BIGINT NOT NULL,
    PRIMARY KEY (producto_id, relacionado_id),
    CONSTRAINT fk_pr_producto   FOREIGN KEY (producto_id)    REFERENCES producto(id),
    CONSTRAINT fk_pr_relacionado FOREIGN KEY (relacionado_id) REFERENCES producto(id)
);

-- ---------------------------------------------------------------
-- Lotes y fotografías
-- ---------------------------------------------------------------

-- Lote usa PK de tipo String (manejada manualmente desde la app)
CREATE TABLE lote (
    lote              VARCHAR(255) PRIMARY KEY,
    precio            REAL,
    precio_venta      REAL,
    precio_descuento  REAL,
    fecha_vencimiento DATE,
    producto_id       BIGINT NOT NULL,
    CONSTRAINT fk_lote_producto FOREIGN KEY (producto_id) REFERENCES producto(id)
);

CREATE TABLE fotografia (
    id          BIGSERIAL    PRIMARY KEY,
    url         VARCHAR(255),
    orden       INTEGER,
    producto_id BIGINT,
    CONSTRAINT fk_fotografia_producto FOREIGN KEY (producto_id) REFERENCES producto(id)
);

-- ---------------------------------------------------------------
-- Ventas y relacionados
-- ---------------------------------------------------------------

CREATE TABLE ventas (
    id             SERIAL       PRIMARY KEY,
    codigo_venta   VARCHAR(255) NOT NULL UNIQUE,
    fecha          DATE         NOT NULL,
    cliente_nombre VARCHAR(255),
    estado         VARCHAR(50)  NOT NULL,
    es_credito     BOOLEAN,
    monto_credito  REAL,
    sucursal_id    BIGINT,
    cliente_id     INTEGER,
    CONSTRAINT fk_venta_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursal(id),
    CONSTRAINT fk_venta_cliente  FOREIGN KEY (cliente_id)  REFERENCES clientes(id)
);

CREATE TABLE creditos (
    id         SERIAL  PRIMARY KEY,
    monto      REAL    NOT NULL,
    fecha      DATE    NOT NULL,
    cliente_id INTEGER NOT NULL,
    venta_id   INTEGER,
    CONSTRAINT fk_credito_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_credito_venta   FOREIGN KEY (venta_id)   REFERENCES ventas(id)
);

CREATE TABLE pagos_credito (
    id         BIGSERIAL PRIMARY KEY,
    credito_id INTEGER   NOT NULL,
    monto      REAL      NOT NULL,
    fecha      DATE      NOT NULL,
    CONSTRAINT fk_pago_credito FOREIGN KEY (credito_id) REFERENCES creditos(id)
);

CREATE TABLE detalle_venta (
    id       BIGSERIAL    PRIMARY KEY,
    cantidad BIGINT,
    venta_id INTEGER      NOT NULL,
    lote_id  VARCHAR(255) NOT NULL,
    CONSTRAINT fk_dv_venta FOREIGN KEY (venta_id) REFERENCES ventas(id),
    CONSTRAINT fk_dv_lote  FOREIGN KEY (lote_id)  REFERENCES lote(lote)
);

CREATE TABLE devoluciones_venta (
    id                SERIAL       PRIMARY KEY,
    codigo_devolucion VARCHAR(255) NOT NULL UNIQUE,
    fecha             DATE         NOT NULL,
    motivo            VARCHAR(500),
    venta_id          INTEGER      NOT NULL,
    procesada         BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_devolucion_venta FOREIGN KEY (venta_id) REFERENCES ventas(id)
);

CREATE TABLE detalle_devolucion_venta (
    id                  BIGSERIAL    PRIMARY KEY,
    cantidad            BIGINT,
    devolucion_id       INTEGER      NOT NULL,
    detalle_venta_id    BIGINT       NOT NULL,
    razon_devolucion    VARCHAR(500),
    CONSTRAINT fk_ddv_devolucion    FOREIGN KEY (devolucion_id)    REFERENCES devoluciones_venta(id),
    CONSTRAINT fk_ddv_detalle_venta FOREIGN KEY (detalle_venta_id) REFERENCES detalle_venta(id)
);

-- ---------------------------------------------------------------
-- Solicitudes entre sucursales
-- ---------------------------------------------------------------

CREATE TABLE solicitud (
    id           BIGSERIAL    PRIMARY KEY,
    fecha        TIMESTAMP,
    estado       VARCHAR(255),
    observacion  VARCHAR(255),
    sucursal_id  BIGINT       NOT NULL,
    CONSTRAINT fk_solicitud_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursal(id)
);

CREATE TABLE detalle_solicitud (
    id              BIGSERIAL PRIMARY KEY,
    cantidad        BIGINT,
    solicitud_id    BIGINT    NOT NULL,
    producto_id     BIGINT    NOT NULL,
    cantidad_envida BIGINT,
    CONSTRAINT fk_ds_solicitud FOREIGN KEY (solicitud_id) REFERENCES solicitud(id),
    CONSTRAINT fk_ds_producto  FOREIGN KEY (producto_id)  REFERENCES producto(id)
);

-- ---------------------------------------------------------------
-- Autorizaciones de crédito
-- ---------------------------------------------------------------

CREATE TABLE autorizaciones_limite_credito (
    id                   SERIAL       PRIMARY KEY,
    cliente_id           INTEGER      NOT NULL,
    limite_credito       REAL         NOT NULL,
    estado               VARCHAR(50)  NOT NULL,
    fecha_solicitud      TIMESTAMP    NOT NULL,
    fecha_autorizacion   TIMESTAMP,
    razon_rechazo        VARCHAR(255),
    CONSTRAINT fk_alc_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

-- ---------------------------------------------------------------
-- Compras a proveedores
-- ---------------------------------------------------------------

CREATE TABLE compra (
    id           BIGSERIAL PRIMARY KEY,
    proveedor_id BIGINT    NOT NULL,
    fecha        TIMESTAMP,
    CONSTRAINT fk_compra_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedor(id)
);

CREATE TABLE detalle_compra (
    id       BIGSERIAL    PRIMARY KEY,
    cantidad BIGINT,
    compra_id BIGINT      NOT NULL,
    lote_id  VARCHAR(255) NOT NULL,
    CONSTRAINT fk_dc_compra FOREIGN KEY (compra_id) REFERENCES compra(id),
    CONSTRAINT fk_dc_lote   FOREIGN KEY (lote_id)   REFERENCES lote(lote)
);

-- ---------------------------------------------------------------
-- Stock por sucursal
-- ---------------------------------------------------------------

CREATE TABLE sucursal_lote (
    id          BIGSERIAL    PRIMARY KEY,
    cantidad    BIGINT,
    lote_id     VARCHAR(255) NOT NULL,
    sucursal_id BIGINT       NOT NULL,
    CONSTRAINT fk_sl_lote     FOREIGN KEY (lote_id)     REFERENCES lote(lote),
    CONSTRAINT fk_sl_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursal(id)
);

-- ---------------------------------------------------------------
-- Índices de performance
-- ---------------------------------------------------------------

CREATE INDEX idx_producto_codigo            ON producto(codigo);
CREATE INDEX idx_producto_proveedor         ON producto(proveedor_id);
CREATE INDEX idx_lote_producto              ON lote(producto_id);
CREATE INDEX idx_ventas_codigo              ON ventas(codigo_venta);
CREATE INDEX idx_ventas_cliente             ON ventas(cliente_id);
CREATE INDEX idx_ventas_fecha               ON ventas(fecha);
CREATE INDEX idx_detalle_venta_venta        ON detalle_venta(venta_id);
CREATE INDEX idx_detalle_venta_lote         ON detalle_venta(lote_id);
CREATE INDEX idx_creditos_cliente           ON creditos(cliente_id);
CREATE INDEX idx_creditos_venta             ON creditos(venta_id);
CREATE INDEX idx_pagos_credito_credito      ON pagos_credito(credito_id);
CREATE INDEX idx_devoluciones_venta_venta   ON devoluciones_venta(venta_id);
CREATE INDEX idx_detalle_devolucion         ON detalle_devolucion_venta(devolucion_id);
CREATE INDEX idx_solicitud_sucursal         ON solicitud(sucursal_id);
CREATE INDEX idx_compra_proveedor           ON compra(proveedor_id);
CREATE INDEX idx_sucursal_lote_lote         ON sucursal_lote(lote_id);
CREATE INDEX idx_sucursal_lote_sucursal     ON sucursal_lote(sucursal_id);
