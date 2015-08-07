INSERT INTO PersonIdentifier (registrypatientid, assigningauthority, patientid, deleted, merged, survivingpatientid) VALUES
('3e7593b4-3c40-11e5-a151-feff819cdc9f', '&1.2.208.176.1.2&ISO', '2512484916', 'N', 'N', null),
('3e7595c6-3c40-11e5-a151-feff819cdc9f', '&1.2.208.176.1.2&ISO', '2512688916', 'N', 'N', null),
('3e759080-3c40-11e5-a151-feff819cdc9f', '&1.2.208.176.1.2&ISO', '1103811376', 'N', 'N', null),
('3e7596d4-3c40-11e5-a151-feff819cdc9f', '&1.2.208.176.1.2&ISO', '1212852635', 'N', 'N', null),
('3e7597ba-3c40-11e5-a151-feff819cdc9f', '&1.2.208.176.1.2&ISO', '1105491135', 'N', 'N', null),
('3e759882-3c40-11e5-a151-feff819cdc9f', '&1.2.208.176.1.2&ISO', '1206322639', 'N', 'N', null);

INSERT INTO Person (id, home, lid, objecttype, status, versionname, comment_, personname_firstname, personname_middlename, personname_lastname) VALUES
('2512484916^^^&1.2.208.176.1.2&ISO', '', '2512484916^^^&1.2.208.176.1.2&ISO', 'urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Person:User', 'urn:oasis:names:tc:ebxml-regrep:StatusType:Submitted', '1.0', 'Danish Test', 'Nancy Ann', '', 'Berggren'),
('2512688916^^^&1.2.208.176.1.2&ISO', '', '2512688916^^^&1.2.208.176.1.2&ISO', 'urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Person:User', 'urn:oasis:names:tc:ebxml-regrep:StatusType:Submitted', '1.0', 'OpenTele Test', 'Lene', '', 'Jensen'),
('1103811376^^^&1.2.208.176.1.2&ISO', '', '1103811376^^^&1.2.208.176.1.2&ISO', 'urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Person:User', 'urn:oasis:names:tc:ebxml-regrep:StatusType:Submitted', '1.0', 'OpenTele Test', 'Kiran', '', 'Liaqat'),
('1212852635^^^&1.2.208.176.1.2&ISO', '', '1212852635^^^&1.2.208.176.1.2&ISO', 'urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Person:User', 'urn:oasis:names:tc:ebxml-regrep:StatusType:Submitted', '1.0', 'OpenTele Test', 'Svend', '', 'Andersson'),
('1105491135^^^&1.2.208.176.1.2&ISO', '', '1105491135^^^&1.2.208.176.1.2&ISO', 'urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Person:User', 'urn:oasis:names:tc:ebxml-regrep:StatusType:Submitted', '1.0', 'OpenTele Test', 'Carl', '', 'Petersen'),
('1206322639^^^&1.2.208.176.1.2&ISO', '', '1206322639^^^&1.2.208.176.1.2&ISO', 'urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:Person:User', 'urn:oasis:names:tc:ebxml-regrep:StatusType:Submitted', '1.0', 'OpenTele Test', 'John', '', 'Hansen');
