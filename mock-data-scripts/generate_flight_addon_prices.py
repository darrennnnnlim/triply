import random

def create_flight_info(data_str):
	# Remove the parentheses and split by commas
	data_str = data_str.strip('()')
	parts = data_str.split(', ')

	# Parse the individual parts
	airline_id = int(parts[0])
	flight_number = parts[1].strip("'")  # Remove the single quotes around the flight number
	origin = parts[2].strip("'")
	destination = parts[3].strip("'")
	departure_time = parts[4].strip("'")
	arrival_time = parts[5].strip("'")
	created_dt = parts[6]  # 'NOW()' as a placeholder
	created_by = parts[7].strip("'")
	updated_dt = parts[8]  # 'NOW()' as a placeholder
	updated_by = parts[9].strip("'")

	# Create the dictionary
	flight_info = {
	    "airline_id": airline_id,
	    "flight_number": flight_number,
	    "origin": origin,
	    "destination": destination,
	    "departure_time": departure_time,
	    "arrival_time": arrival_time,
	    "created_dt": created_dt,
	    "created_by": created_by,
	    "updated_dt": updated_dt,
	    "updated_by": updated_by
	}

	return flight_info

def generate_random_flight_addon_price_for_all_addons(idx, flight_addon_ids, flight_info):
    random_min_price = 10
    random_max_price = 100

    prices = []
    for flight_addon_id in flight_addon_ids:
        row_data = (
            idx,
            flight_addon_id,
            round(random.uniform(random_min_price, random_max_price), 2),  # random flight price
            'NOW()',
            'SYSTEM',
            'NOW()',
            'SYSTEM'
        )
        prices.append(str(row_data))
    return prices

file_path = "./mock_flight_table.sql"
with open(file_path, 'r') as file:
    idx = 1 # postgres is 1-based
    flight_addon_ids = [1,2,3,4]

    sqlValueStmts = []

    for line in file:
        if line.strip().startswith('('):
            print(create_flight_info(line))
            flight_addon_price_sqls = generate_random_flight_addon_price_for_all_addons(idx, flight_addon_ids, create_flight_info(line))
            sqlValueStmts.extend(flight_addon_price_sqls)
            idx += 1

    with open("./mock_flight_addon_price_table.sql", "w") as file:
        file.write("INSERT INTO Flight_Addon_Price (flight_id, flight_addon_id, price, created_dt, created_by, updated_dt, updated_by) VALUES" + "\n")
        # Iterate over each string in the list
        for idx, line in enumerate(sqlValueStmts):
            # Write the string followed by a newline character
            if idx == 0:
                file.write(line)
            else:
                file.write(",\n" + line)
        file.write(";\n")