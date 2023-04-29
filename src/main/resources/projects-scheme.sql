DROP TABLE IF EXISTS step;
DROP TABLE IF EXISTS material;
DROP TABLE IF EXISTS project_category;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS category;

CREATE TABLE category (
    category_id INT AUTO_INCREMENT NOT NULL,
    category_name TEXT,
    PRIMARY KEY (category_id)
);

CREATE TABLE project (
    project_id INT AUTO_INCREMENT NOT NULL,
    project_name TEXT,
    estimated_hours INT,
    actual_hours INT,
    difficulty INT,
    notes TEXT,
    primary KEY (project_id)
);

CREATE TABLE step (
    step_id INT AUTO_INCREMENT NOT NULL,
    project_id INT,
    step_text TEXT,
    step_order INT,
    primary key (step_id),
    foreign key (project_id) references project (project_id) ON DELETE CASCADE
);

CREATE TABLE material (
    material_id INT AUTO_INCREMENT NOT NULL,
    project_id INT,
    material_name TEXT,
    num_required INT,
    cost INT,
    PRIMARY KEY (material_id),
    FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

CREATE TABLE project_category (
	unique_ID INT AUTO_increment not null,
    project_id INT NOT NULL,
    category_id INT NOT NULL,
    primary key (unique_ID),
    FOREIGN KEY(project_id) REFERENCES project (project_id) ON DELETE cascade,
    FOREIGN key (category_id) REFERENCES project (project_id) ON DELETE CASCADE
);


