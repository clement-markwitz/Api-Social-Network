def create_label_constraint(graph, coll):
    label = coll[:1].upper() + coll[1:]
    graph.run(f"CREATE CONSTRAINT IF NOT EXISTS FOR (n:`{label}`) REQUIRE n.mongo_id IS UNIQUE")
    return label