package com.example.aspirepath

import android.content.Context
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class ChatbotActivity : AppCompatActivity() {

    // JavaScript Interface to handle communication from web page to Android
    class WebAppInterface(private val mContext: Context) {
        @JavascriptInterface
        fun goBack() {
            if (mContext is ChatbotActivity) {
                mContext.finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        // Set up toolbar
        supportActionBar?.hide() // Hide ActionBar for immersive web experience

        val webView = findViewById<WebView>(R.id.webViewChatbot)
        
        // Configure WebView settings
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            databaseEnabled = true
            allowFileAccess = true
        }

        // Add JavaScript Interface
        webView.addJavascriptInterface(WebAppInterface(this), "Android")

        // Set WebViewClient
        webView.webViewClient = WebViewClient()

        // HTML content with fixed header and scrollable body
        val htmlContent = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>AspireBot</title>
                <!-- Botpress Scripts -->
                <script src="https://cdn.botpress.cloud/webchat/v3.6/inject.js"></script>
                <script src="https://files.bpcontent.cloud/2025/07/17/13/20250717130018-7HY46GKI.js" defer></script>
                <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
                <style>
                    body, html { 
                        margin: 0; 
                        padding: 0; 
                        width: 100%;
                        height: 100%;
                        font-family: 'Poppins', sans-serif;
                        background: linear-gradient(180deg, #E8EAF6 0%, #F3E5F5 100%);
                        color: #2D3242;
                        overflow: hidden; /* Prevent body scroll, use inner scroll */
                    }
                    .main-wrapper {
                        display: flex;
                        flex-direction: column;
                        height: 100%;
                        max-width: 600px;
                        margin: 0 auto;
                    }
                    
                    /* Fixed Header Section */
                    .header-section {
                        flex: 0 0 auto;
                        display: flex;
                        flex-direction: column;
                        align-items: center;
                        padding: 30px 20px 10px 20px;
                        z-index: 10;
                    }

                    /* Scrollable Content Section */
                    .scroll-content {
                        flex: 1;
                        overflow-y: auto;
                        padding: 0 20px 30px 20px;
                        width: 100%;
                        box-sizing: border-box;
                    }
                    
                    /* Hide scrollbar for cleaner look */
                    .scroll-content::-webkit-scrollbar {
                        display: none;
                    }
                    .scroll-content {
                        -ms-overflow-style: none;
                        scrollbar-width: none;
                    }
                    
                    /* Logo & Header Elements */
                    .logo-container {
                        width: 100px;
                        height: 100px;
                        background-color: white;
                        border-radius: 50%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        margin-bottom: 20px;
                        box-shadow: 0 8px 16px rgba(123, 104, 238, 0.15);
                        border: 4px solid white;
                        flex-shrink: 0;
                    }
                    .logo {
                        width: 100%;
                        height: 100%;
                        object-fit: contain;
                        padding: 10px;
                        box-sizing: border-box;
                    }
                    h1 {
                        color: #2D3242; /* Dark Blue Text */
                        margin: 0 0 8px 0;
                        font-size: 26px;
                        font-weight: 700;
                        text-align: center;
                    }
                    .subtitle {
                        color: #7B68EE; /* Primary Light Purple */
                        margin: 0 0 24px 0;
                        font-size: 14px;
                        font-weight: 500;
                        text-align: center;
                    }

                    /* Buttons */
                    .btn {
                        padding: 16px;
                        border-radius: 16px;
                        border: none;
                        font-size: 16px;
                        font-weight: 600;
                        cursor: pointer;
                        transition: all 0.2s ease;
                        width: 100%;
                        margin-bottom: 12px;
                        box-shadow: 0 4px 10px rgba(0,0,0,0.05);
                        font-family: 'Poppins', sans-serif;
                    }
                    .btn:active {
                        transform: scale(0.98);
                    }
                    .btn-primary {
                        background-color: #7B68EE;
                        color: white;
                        box-shadow: 0 4px 15px rgba(123, 104, 238, 0.4);
                    }
                    .btn-secondary {
                        background-color: white;
                        color: #7B68EE;
                        border: 2px solid #7B68EE;
                    }

                    /* Cards */
                    .card {
                        background-color: white;
                        border-radius: 24px;
                        padding: 24px;
                        width: 100%;
                        box-sizing: border-box;
                        margin-top: 16px;
                        box-shadow: 0 4px 20px rgba(0,0,0,0.03);
                    }
                    .card-title {
                        color: #2D3242; /* Dark Blue Heading */
                        font-size: 18px;
                        font-weight: 700;
                        margin-bottom: 12px;
                        text-align: center;
                    }
                    .card-text {
                        color: #666666;
                        font-size: 14px;
                        line-height: 1.6;
                        text-align: center;
                    }

                    /* FAQ Accordion */
                    .accordion-item {
                        border: 1px solid #E0E0E0;
                        border-radius: 12px;
                        margin-bottom: 10px;
                        overflow: hidden;
                        transition: all 0.3s ease;
                    }
                    .accordion-header {
                        padding: 16px;
                        background-color: #F8F9FA;
                        cursor: pointer;
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        font-weight: 600;
                        font-size: 14px;
                        color: #4A4A4A; /* Slightly darker than card text */
                    }
                    .accordion-header .icon {
                        color: #7B68EE;
                        transition: transform 0.3s ease;
                    }
                    .accordion-content {
                        max-height: 0;
                        overflow: hidden;
                        transition: max-height 0.3s ease-out;
                        background-color: white;
                    }
                    .accordion-content p {
                        padding: 16px;
                        margin: 0;
                        font-size: 13px;
                        color: #666666;
                        line-height: 1.5;
                        text-align: left;
                    }
                    
                    /* Open State */
                    .accordion-item.active {
                        border-color: #7B68EE;
                        box-shadow: 0 2px 8px rgba(123, 104, 238, 0.1);
                    }
                    .accordion-item.active .accordion-header {
                        background-color: #F3E5F5;
                        color: #7B68EE;
                    }
                    .accordion-item.active .icon {
                        transform: rotate(180deg);
                    }
                    .accordion-item.active .accordion-content {
                        max-height: 200px; /* Arbitrary large height */
                    }

                </style>
            </head>
            <body>
                <div class="main-wrapper">
                    <!-- Fixed Header Region -->
                    <div class="header-section">
                        <div class="logo-container">
                            <img src="file:///android_res/drawable/aspirebot.png" class="logo" alt="AspireBot Logo">
                        </div>
                        <h1>Welcome to AspireBot</h1>
                        <p class="subtitle">Your personal AI career guidance assistant.</p>
                        
                        <button id="open-aspirebot-chat" class="btn btn-primary" onclick="openChat()">Click to Chat with Bot</button>
                        <button class="btn btn-secondary" onclick="goBack()">Back to Home</button>
                    </div>

                    <!-- Scrollable Content Region -->
                    <div class="scroll-content">
                        <!-- About Card -->
                        <div class="card">
                            <div class="card-title">About AspirePath</div>
                            <p class="card-text">
                                AspirePath is your comprehensive career planning companion. We provide personalized roadmaps, 
                                detailed insights into competitive exams, and success stories to help you navigate your 
                                professional journey from start to finish.
                            </p>
                        </div>

                        <!-- FAQ Card -->
                        <div class="card">
                            <div class="card-title">Common FAQs</div>
                            
                            <div class="accordion-item">
                                <div class="accordion-header" onclick="toggleAccordion(this)">
                                    What are the features?
                                    <span class="icon">▼</span>
                                </div>
                                <div class="accordion-content">
                                    <p>AspirePath offers personalized career quizzes, step-by-step career roadmaps, information on competitive exams, college finder, and scholarship opportunities tailored to your profile.</p>
                                </div>
                            </div>

                            <div class="accordion-item">
                                <div class="accordion-header" onclick="toggleAccordion(this)">
                                    Why use this app?
                                    <span class="icon">▼</span>
                                </div>
                                <div class="accordion-content">
                                    <p>We simplify the complex process of career planning by aggregating trusted resources in one place. Saving you time and providing clarity when you feel confused about your future path.</p>
                                </div>
                            </div>
                            
                            <div class="accordion-item">
                                <div class="accordion-header" onclick="toggleAccordion(this)">
                                    How do I start?
                                    <span class="icon">▼</span>
                                </div>
                                <div class="accordion-content">
                                    <p>Start by taking our Career Quiz on the home page to identify your interests, or explore Trending Jobs to see what's in demand right now!</p>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <script>
                    function openChat() {
                        // Check for the modern WebChat API
                        if (window.botpressWebChat) {
                            window.botpressWebChat.sendEvent({ type: 'show' });
                            window.botpressWebChat.sendEvent({ type: 'open' });
                        } else {
                            // If not loaded yet, show a tiny loading state on the button
                            const btn = document.getElementById("open-aspirebot-chat");
                            
                            // Re-check every 300ms
                            const checkInterval = setInterval(() => {
                                if (window.botpressWebChat) {
                                    window.botpressWebChat.sendEvent({ type: 'show' });
                                    window.botpressWebChat.sendEvent({ type: 'open' });
                                    btn.innerText = originalText;
                                    clearInterval(checkInterval);
                                }
                            }, 300);
                        }
                    }

                    function goBack() {
                        if (window.Android) {
                            Android.goBack();
                        }
                    }

                    function toggleAccordion(element) {
                        const item = element.parentElement;
                        const isActive = item.classList.contains('active');
                        
                        if (!isActive) {
                            item.classList.add('active');
                        } else {
                            item.classList.remove('active');
                        }
                    }

                    window.addEventListener('load', function() {
                        // Ensure Botpress loads silently in background
                        // We do NOT auto-open it anymore as per UI design
                    });
                </script>
            </body>
            </html>
        """.trimIndent()


        webView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "UTF-8", null)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
