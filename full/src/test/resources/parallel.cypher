MATCH (n:Polling) SET n.test = date() RETURN n;
