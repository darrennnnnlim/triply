import random
import re

# Read Hotel_Room_Type SQL and extract room type tuples (skip the first line)
with open("mock_hotel_room_type_table.sql", "r") as file:
    lines = file.readlines()[1:]

room_type_data = "".join(lines)
room_tuples = re.findall(r"\(([^)]+)\)", room_type_data)

# Define pricing rules by room category
category_price_bumps = {
    "basic": (50, 70),
    "deluxe": (80, 120),
    "suite": (130, 160)
}

# Helper function to classify room name
def classify_room(name: str):
    name = name.lower()
    if any(keyword in name for keyword in ["classic", "standard"]):
        return "basic"
    elif any(keyword in name for keyword in ["superior", "deluxe", "executive"]):
        return "deluxe"
    elif any(keyword in name for keyword in ["honeymoon", "suite", "presidential"]):
        return "suite"
    return "basic"  # default fallback

# Generate price SQL
room_price_id = 1
sql_lines = []

for tup in room_tuples:
    parts = [p.strip() for p in tup.split(",")]
    room_type_id = int(parts[0])
    name = parts[2].strip().strip("'")
    base_price = float(parts[3])

    category = classify_room(name)
    bump_range = category_price_bumps[category]
    price = round(base_price + random.uniform(*bump_range), 2)

    sql = f"( {room_price_id}, {room_type_id}, {price}, '2025-07-31', '2025-08-02', NOW(), 'SYSTEM', NOW(), 'SYSTEM')"
    sql_lines.append(sql)
    room_price_id += 1

# Write to output file
with open("mock_hotel_room_price_table.sql", "w") as f:
    f.write("INSERT INTO Hotel_Room_Price (id, hotel_room_type_id, price, start_date, end_date, created_dt, created_by, updated_dt, updated_by) VALUES\n")
    f.write(",\n".join(sql_lines) + ";\n")
