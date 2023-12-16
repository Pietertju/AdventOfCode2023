# Written on phone in the train 
def getDirection(character, direction):
    directions = []
    i,j = direction
    if character == '.':
        directions.append(direction)
    elif character == '/':
        directions.append((-1*j, -1*i))
    elif character == '\\':
        directions.append((j,i))
    elif character == '-':
        if not j == 0:
              directions.append(direction)
        else:
            directions.append((0,i))
            directions.append((0,-1*i))
    elif character == '|':
        if not i == 0:
            directions.append(direction)
        else:
            directions.append((j,0))
            directions.append((-1*j,0))      
    return directions
def getCharge(startPos, startDir, inputArray):
    charge = 0
    startX, startY = startPos
    startI, startJ = startDir
    inputNum = [[0]*len(inputArray[0]) for _ in range(len(inputArray))]
    
    savedLocations =[[[]]*len(inputArray[0]) for _ in range(len(inputArray))]
    for x in range(len(inputArray)):
        for y in range(len(inputArray[0])):
            savedLocations[x][y] = []
    queue=[]
    queue.append(((startX,startY),(startI,startJ)))
    while(len(queue)>0):
        p,d = queue.pop(0)
        x,y = p
        i,j = d
        if(x<0 or x>=len(inputArray) or y<0 or y>=len(inputArray[0])):
            continue          
        else:
            cycle = False
            for foundDirs in savedLocations[x][y]:
                i2, j2 = foundDirs
                if i2 == i and j2 == j:
                    cycle = True
                    break
            if(cycle):
                continue
            inputNum[x][y]+=1
            savedLocations[x][y].append(d)
            directions = getDirection(inputArray[x][y], (i,j))
            for direction in directions:
                newI, newJ = direction
                queue.append(((x+newI,y+newJ),(newI,newJ)))
    for i in range(len(inputNum)):
        for j in range(len(inputNum[0])):
            score = 1 if inputNum[i][j] > 0 else 0
            charge += score
    return charge
with open("input.txt") as file:
    input = file.read()

    inputArray = input.split("\n")
    ans = getCharge((0,0), (0,1), inputArray)
    highestCharge = 0
    for x in range(len(inputArray)):
        charge = getCharge((x,0), (0,1), inputArray)
        if charge > highestCharge:
            highestCharge = charge
        charge = getCharge((x,len(inputArray)-1), (0,-1), inputArray)
        if charge > highestCharge:
            highestCharge = charge
        print("X: ", x)
    for y in range(len(inputArray[0])):
        charge = getCharge((0,y), (1,0), inputArray)
        if charge > highestCharge:
            highestCharge = charge
        charge = getCharge((len(inputArray[0])-1,y), (-1,0), inputArray)
        if charge > highestCharge:
            highestCharge = charge
        print("Y: ", y)
        
    print(highestCharge)
    