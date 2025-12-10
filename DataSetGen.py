import random

first_names = ["John","Sarah","Michael","Emily","David","Linda","James","Barbara","Robert","Mary","Alice","Martin","Joe","Bob","Brett","Linda","Susan","Karen","Nancy","Lisa","Betty","Jameson","Catherine","Eleanor","Frank","George","Helen","Irene"]
last_names = ["Smith","Johnson","Williams","Brown","Jones","Miller","Davis","Garcia","Martinez","Hernandez","Chekov","Doe","Ronstad","Hardleaf","Hasacat","Black","White","Green","Blue","Yellow","Orange","Purple","Gray","Silver","Gold","Bronze","Copper","Iron"]

rows = []
rows.append("Id,firstName,lastName,salary,managerId")

for i in range(1,1000):
    first = random.choice(first_names)
    last = random.choice(last_names)
    salary = random.randint(30000,2000000)
    # 0.1% chance to be a top-level manager (no managerId)
    if random.random() < 0.001:
        manager = ""
    else:
        manager = str(random.randint(1,1000-i))
    rows.append(f"{i},{first},{last},{salary},{manager}")

# Save to CSV
with open("employeesGen.csv","w") as f:
    f.write("\n".join(rows))