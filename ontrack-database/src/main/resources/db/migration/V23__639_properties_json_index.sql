-- 23. Properties JSON index

CREATE INDEX idx_properties_json ON properties USING gin (json);
