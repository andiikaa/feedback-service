// average temperature in the kitchen
MATCH (unit)<-[:unitOfMeasure]-(value)<-[:hasStateValue]-(state:TemperatureState)<-[:hasState]-(sensor)-[:isIn]->(room:Kitchen) 
WHERE room.name = "Kitchen_Mueller" AND LOWER(unit.name) =~ ".*celsius.*"
RETURN avg(toFloat(value.realStateValue)) AS celsius
