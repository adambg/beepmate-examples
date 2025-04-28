import urllib.request
import urllib.parse
import http.client
import mimetypes
import os

# Example 1: Send a text message
def send_message(key, phone_id, message):
	# URL encode the message
	encoded_message = urllib.parse.quote(message)
	url = f"https://beepmate.io/send?key={key}&id={phone_id}&msg={encoded_message}"
	
	# Send GET request
	with urllib.request.urlopen(url) as response:
		return response.read().decode('utf-8')

# Example 2: Send a file
def send_file(key, phone_id, file_path):
	file_name = os.path.basename(file_path)
	
	# Create boundary for multipart form data
	boundary = '----WebKitFormBoundary7MA4YWxkTrZu0gW'
	
	# Prepare headers
	headers = {
		'Content-Type': f'multipart/form-data; boundary={boundary}'
	}
	
	# Prepare form data
	with open(file_path, 'rb') as f:
		file_data = f.read()
	
	# Build the multipart form data manually
	body = []
	# Add key parameter
	body.append(f'--{boundary}'.encode())
	body.append('Content-Disposition: form-data; name="key"'.encode())
	body.append(''.encode())
	body.append(key.encode())
	
	# Add id parameter
	body.append(f'--{boundary}'.encode())
	body.append('Content-Disposition: form-data; name="id"'.encode())
	body.append(''.encode())
	body.append(phone_id.encode())
	
	# Add file parameter
	body.append(f'--{boundary}'.encode())
	body.append(f'Content-Disposition: form-data; name="file"; filename="{file_name}"'.encode())
	body.append(f'Content-Type: {mimetypes.guess_type(file_path)[0] or "application/octet-stream"}'.encode())
	body.append(''.encode())
	body.append(file_data)
	
	# Close the form
	body.append(f'--{boundary}--'.encode())
	
	# Join all parts with CRLF
	body_data = b'\r\n'.join(body)
	
	# Send the POST request
	conn = http.client.HTTPSConnection("beepmate.io")
	conn.request("POST", "/send", body_data, headers)
	response = conn.getresponse()
	return response.read().decode()

# Usage examples
if __name__ == "__main__":
	API_KEY = "MyKey"
	PHONE_ID = "19291111111"
	
	# Send a text message
	result = send_message(API_KEY, PHONE_ID, "Hello World")
	print(result)
	
	# Send a file
	file_result = send_file(API_KEY, PHONE_ID, "c:\\somefile.pdf")
	print(file_result)
	
