package main

import (
	"bytes"
	"fmt"
	"io"
	"io/ioutil"
	"mime/multipart"
	"net/http"
	"net/url"
	"os"
	"path/filepath"
)

// SendMessage sends a text message via GET request
func SendMessage(key, phoneID, message string) (string, error) {
	// URL encode parameters
	params := url.Values{}
	params.Add("key", key)
	params.Add("id", phoneID)
	params.Add("msg", message)

	// Create URL with query parameters
	requestURL := "https://beepmate.io/send?" + params.Encode()

	// Send GET request
	resp, err := http.Get(requestURL)
	if err != nil {
		return "", err
	}
	defer resp.Body.Close()

	// Read response
	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return "", err
	}

	return string(body), nil
}

// SendFile sends a file via POST request with multipart form data
func SendFile(key, phoneID, filePath string) (string, error) {
	// Create a buffer to store the multipart form data
	var requestBody bytes.Buffer
	writer := multipart.NewWriter(&requestBody)

	// Add form fields
	writer.WriteField("key", key)
	writer.WriteField("id", phoneID)

	// Add file field
	file, err := os.Open(filePath)
	if err != nil {
		return "", err
	}
	defer file.Close()

	part, err := writer.CreateFormFile("file", filepath.Base(filePath))
	if err != nil {
		return "", err
	}

	// Copy file content to form field
	_, err = io.Copy(part, file)
	if err != nil {
		return "", err
	}

	// Close the writer
	writer.Close()

	// Create and execute request
	req, err := http.NewRequest("POST", "https://beepmate.io/send", &requestBody)
	if err != nil {
		return "", err
	}

	req.Header.Set("Content-Type", writer.FormDataContentType())

	// Send request
	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return "", err
	}
	defer resp.Body.Close()

	// Read response
	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return "", err
	}

	return string(body), nil
}

func main() {
	apiKey := "MyKey"
	phoneID := "19291111111"

	// Send text message
	result, err := SendMessage(apiKey, phoneID, "Hello World")
	if err != nil {
		fmt.Println("Error sending message:", err)
	} else {
		fmt.Println("Message sent, response:", result)
	}

	// Send file
	fileResult, err := SendFile(apiKey, phoneID, "somefile.pdf")
	if err != nil {
		fmt.Println("Error sending file:", err)
	} else {
		fmt.Println("File sent, response:", fileResult)
	}
}
