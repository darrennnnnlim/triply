import random
import re

# Read hotel data from file
# Read hotel data from lines AFTER the first one
with open("mock_hotel_table.sql", "r") as file:
    lines = file.readlines()[1:]  # Skip first line

# Join the lines and extract hotel tuples
hotel_data = "".join(lines)
hotel_tuples = re.findall(r"\(([^)]+)\)", hotel_data)

# Parse hotel_id from each tuple
hotels = []
for tup in hotel_tuples:
    parts = tup.split(',')
    hotel_id = int(parts[0].strip())
    hotels.append(hotel_id)

room_categories = [
    {
        "names": ["Classic Room", "Standard King"],
        "price_multiplier": (0.75, 1.5),
        "capacity": 2
    },
    {
        "names": ["Superior Room", "Deluxe Room", "Executive Room"],
        "price_multiplier": (2, 3.5),
        "capacity": 4
    },
    {
        "names": ["Honeymoon Suite", "Executive Suite", "Presidential Suite"],
        "price_multiplier": (4, 5.5),
        "capacity": 8
    }
]

base_price = 200
room_id = 1
sql_lines = []

for hotel in hotels:
    hotel_id = hotel
    for category in room_categories:
        name = random.choice(category["names"])
        multiplier = round(random.uniform(*category["price_multiplier"]), 2)
        price = round(base_price * multiplier, 2)
        capacity = category["capacity"]

        sql = f"( {room_id}, {hotel_id}, '{name}', {price}, {capacity}, NOW(), 'SYSTEM', NOW(), 'SYSTEM')"
        sql_lines.append(sql)
        room_id += 1

with open("mock_hotel_room_type_table.sql", "w") as f:
    f.write("INSERT INTO Hotel_Room_Type (id, hotel_id, name, base_price, capacity, created_dt, created_by, updated_dt, updated_by) VALUES\n")
    f.write(",\n".join(sql_lines) + ";\n")
