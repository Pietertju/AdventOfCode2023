# Please note this code was written on my phone on some online IDE
import time

with open('input.txt') as file:
    start_time = time.time()

    input = file.read()

    inputArray = input.split('\n')
    stars = [[1] * len(inputArray[0]) for _ in range(len(inputArray))]
    starcounts = [[0] * len(inputArray[0]) for _ in range(len(inputArray))]
    answerPart1 = 0
    for i in range(0, len(inputArray)):
        line = inputArray[i]
        startIndex = -1
        endIndex = -1
        inNumber = False
        for j in range(0, len(line)):
            last = False
            if (line[j].isnumeric()):
                if (not inNumber):
                    inNumber = True
                    startIndex = j
                    endIndex = j
                else:
                    endIndex = j
                if (j == len(line) - 1):
                    last = True
            if ((not line[j].isnumeric()) or last):
                if (inNumber):
                    # num found
                    partNumber = False
                    if (i - 1) >= 0:
                        for index in range(max(startIndex - 1, 0), min(endIndex + 2, len(inputArray[i- 1]))):
                            if ((not inputArray[i - 1][index].isnumeric()) and (not inputArray[i - 1][index] == '.')):
                                partNumber = True
                                # checkstar
                                if (inputArray[i - 1][index] == '*'):
                                    stars[i - 1][index] *= int(line[startIndex:endIndex + 1])
                                    starcounts[i - 1][index] += 1
                    if (startIndex - 1) >= 0:
                        if ((not line[startIndex - 1].isnumeric()) and (not line[startIndex - 1] == '.')):
                            partNumber = True
                            # checkstar
                            if (line[startIndex - 1] == '*'):
                                stars[i][startIndex - 1] *= int(line[startIndex:endIndex + 1])
                                starcounts[i][startIndex - 1] += 1
                    if (endIndex + 1 <= len(line) - 1):
                        if ((not line[endIndex + 1].isnumeric()) and (not line[endIndex + 1] == '.')):
                            partNumber = True
                            # checkstar
                            if (line[endIndex + 1] == '*'):
                                stars[i][endIndex + 1] *= int(line[startIndex:endIndex + 1])
                                starcounts[i][endIndex + 1] += 1
                    if (i + 1 < len(inputArray)):
                        for index in range(max(0, startIndex - 1), min(len(inputArray[i + 1]), endIndex + 2)):
                            if ((not inputArray[i + 1][index].isnumeric()) and (not inputArray[i + 1][index] == '.')):
                                partNumber = True
                                # checkstar

                                if (inputArray[i + 1][index] == '*'):
                                    stars[i + 1][index] *= int(line[startIndex:endIndex + 1])
                                    starcounts[i + 1][index] += 1
                    if (partNumber):
                        answerPart1 += int(line[startIndex:endIndex + 1])
                inNumber = False
                startIndex = -1
                endIndex = -1
                
    answerPart2 = 0
    for k in range(0, len(starcounts)):
        for l in range(0, len(starcounts[k])):
            if (starcounts[k][l] == 2):
                answerPart2 += stars[k][l]

    end_time = time.time()
    elapsed_time = (end_time - start_time)*1000       
    print("Part1: ", answerPart1)
    print("Part2: ", answerPart2)
    print("Part 1 and 2 took: ", elapsed_time, " ms combined") 
