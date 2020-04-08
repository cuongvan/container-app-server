import ckanapplib, requests, io, csv
import matplotlib.pyplot as plt

r = requests.get('https://www.stats.govt.nz/assets/Uploads/Annual-enterprise-survey/Annual-enterprise-survey-2018-financial-year-provisional/Download-data/annual-enterprise-survey-2018-financial-year-provisional-size-bands-csv.csv')

values = []
reader = csv.reader(io.StringIO(r.text))
next(reader) # skip header
for row in reader:
    try:
        values.append(float(row[5]))
    except ValueError:
        pass

plt.hist(values)
plt.savefig('values.png')

with open('values.png', 'rb') as f:
    ckanapplib.put_file('values.png', f.read())