import time

def distance(g1, g2, extraColumns, extraRows):
    distance = 0
    distance2 = 0
    distance3 = 0
    (x1, y1) = g1
    (x2, y2) = g2
    for i in range(min(x1, x2)+1, max(x1, x2)+1):
        (expansionPart1, expansionPart2) = extraColumns[i]
        distance += expansionPart1
        distance2 += expansionPart2
    for i in range(min(y1, y2)+1, max(y1, y2)+1):
        (expansionPart1, expansionPart2) = extraRows[i]
        distance += expansionPart1
        distance2 += expansionPart2
    return (distance, distance2)
    
with open('input.txt') as file:
    start_time = time.time()

    input = file.read()

    inputArray = input.split('\n')
    
    expansionPart1 = 2
    expansionPart2 = 10**6
        
    extraColumns = [(expansionPart1, expansionPart2)] * len(inputArray[0])
    extraRows = [(1, 1)] * len(inputArray)
    
    galaxies = []
    
    for i in range(0, len(inputArray)):
        line = inputArray[i]
        index = 0
        foundGalaxy = False
        for character in line:
            if(character == "#"):
                foundGalaxy = True
                extraColumns[index] = (1, 1)
                galaxies.append((index, i))
            index += 1
        if not foundGalaxy:
            extraRows[i] = (expansionPart1, expansionPart2)

    distances = [distance(galaxies[g1], galaxies[g2], extraColumns, extraRows) for g1 in range(len(galaxies)) for g2 in range(g1+1,len(galaxies))]
    
    distancesPart1 = [expansionPart1 for (expansionPart1, expansionPart2) in distances]
    distancesPart2 = [expansionPart2 for (expansionPart1, expansionPart2) in distances]
    
    answerPart1 = sum(distancesPart1)
    answerPart2 = sum(distancesPart2)

    end_time = time.time()
    elapsed_time = (end_time - start_time)*1000       
    print("Part1: ", answerPart1)
    print("Part2: ", answerPart2)
    print("Part 1 and 2 took: ", elapsed_time, " ms combined") 
