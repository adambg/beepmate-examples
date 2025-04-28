<?php

// Function to send a text message via GET request
function sendMessage($key, $phoneId, $message)
{
    // URL encode the message
    $encodedMessage = urlencode($message);
    $url = "https://beepmate.io/send?key={$key}&id={$phoneId}&msg={$encodedMessage}";

    // Send GET request
    $response = file_get_contents($url);

    return $response;
}

// Function to send a file via POST request
function sendFile($key, $phoneId, $filePath)
{
    // Initialize cURL session
    $ch = curl_init();

    // Prepare file for upload
    $fileData = new CURLFile($filePath);

    // Prepare form data
    $postData = [
        'key' => $key,
        'id' => $phoneId,
        'file' => $fileData
    ];

    // Set cURL options
    curl_setopt($ch, CURLOPT_URL, 'https://beepmate.io/send');
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, $postData);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false); // For testing only, consider enabling in production

    // Execute cURL request
    $response = curl_exec($ch);

    // Check for errors
    if (curl_errno($ch)) {
        $response = 'Error: ' . curl_error($ch);
    }

    // Close cURL session
    curl_close($ch);

    return $response;
}

// Usage examples
$apiKey = 'MyKey';
$phoneId = '19291111111';

// Example 1: Send a text message
$messageResult = sendMessage($apiKey, $phoneId, 'Hello World');
echo "Message sent, response: " . $messageResult . "\n";

// Example 2: Send a file
$fileResult = sendFile($apiKey, $phoneId, 'somefile.pdf');
echo "File sent, response: " . $fileResult . "\n";

?>
