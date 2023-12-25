import networkx as nx

G = nx.Graph()

with open('input.txt') as req_file:   
    for line in req_file:
        line = line.split(":")
        source = line[0]
        destination = line[1].split()
        for d in destination:
            G.add_edge(source, d)
          
minimum_cut = nx.minimum_edge_cut(G)
G.remove_edges_from(minimum_cut)

largest_part = len(max(nx.connected_components(G), key=len))
print(minimum_cut)

ans = largest_part*(len(G)-largest_part)
print(ans)
