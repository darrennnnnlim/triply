import random
import re

# Read hotel data from lines AFTER the first one
with open("mock_hotel_table.sql", "r") as file:
    lines = file.readlines()[1:]  # Skip first line

# Extract hotel tuples
hotel_data = "".join(lines)
hotel_tuples = re.findall(r"\(([^)]+)\)", hotel_data)

# Parse hotel_id from each tuple
hotels = []
for tup in hotel_tuples:
    parts = tup.split(',')
    hotel_id = int(parts[0].strip())
    hotels.append(hotel_id)

# Define add-ons and price ranges
addons = [
    ("Spa Access", 20, 30),
    ("Breakfast", 10, 20),
    ("Airport Shuttle", 20, 50),
    ("Late Check-Out", 50, 100),
    ("Early Check-In", 50, 100)
]

addon_id = 1
sql_lines = []

for hotel_id in hotels:
    for name, min_price, max_price in addons:
        price = round(random.uniform(min_price, max_price), 2)
        sql = f"( {addon_id}, {hotel_id}, '{name}', {price}, NOW(), 'SYSTEM', NOW(), 'SYSTEM')"
        sql_lines.append(sql)
        addon_id += 1

# Write to output file
with open("mock_hotel_addon_table.sql", "w") as f:
    f.write("INSERT INTO Hotel_Add_On (id, hotel_id, name, price, created_dt, created_by, updated_dt, updated_by) VALUES\n")
    f.write(",\n".join(sql_lines) + ";\n")
