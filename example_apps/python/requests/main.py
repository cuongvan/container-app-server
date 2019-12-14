import requests

if __name__ == "__main__":
    r = requests.post('https://httpbin.org/post', json={
        'name': 'Cuong Van',
        'phone': '0123456789',
    })
    print(r.json())