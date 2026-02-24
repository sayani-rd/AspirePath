package com.example.aspirepath

sealed class SuccessStoryItem {
    data class Header(val title: String, val streams: List<String> = emptyList()) : SuccessStoryItem()
    data class Story(val name: String, val description: String) : SuccessStoryItem()
}

object SuccessStoryData {
    val items = listOf(
        SuccessStoryItem.Header("Medicine & Science", listOf("Science")),
        SuccessStoryItem.Story(
            "Dr. P. S. Ramani",
            "A world-renowned neurosurgeon known for his pioneering work in neuro-spinal surgery and the \"PLIF\" technique. He is an alumnus of Goa Medical College (GMC)."
        ),
        SuccessStoryItem.Story(
            "Dr. Francisco Luís Gomes",
            "A 19th-century polymath and physician who studied at the historic Medical School of Goa (now GMC). He was an acclaimed economist, writer, and representative in the Portuguese parliament."
        ),
        SuccessStoryItem.Story(
            "Dr. Froilano de Mello",
            "An illustrious microbiologist and former director of the Medical School of Goa who made significant contributions to tropical medicine."
        ),
        SuccessStoryItem.Story(
            "Dr. Miguel Caetano Dias",
            "The only Goan to reach the rank of General in the Portuguese Medical Corps and a former director of GMC who fought to preserve and upgrade its curriculum."
        ),

        SuccessStoryItem.Header("Corporate Leadership & Entrepreneurship", listOf("Commerce", "Science")),
        SuccessStoryItem.Story(
            "Balendu Shrivastava",
            "Head of Measurement at Meta (Facebook) India. He completed his management studies at the Goa Institute of Management (GIM) in 2000."
        ),
        SuccessStoryItem.Story(
            "Abhishek Grover",
            "Vice President of Sales at PepsiCo. He is a 2004 graduate of GIM and an expert in business development."
        ),
        SuccessStoryItem.Story(
            "Vijay Thomas",
            "Founder and CEO of Tangentia, a global digital transformation company. He earned his MBA from GIM."
        ),
        SuccessStoryItem.Story(
            "Samarjeet Singh",
            "Founder and CEO of Iksula, an e-retail consultancy. He is a 1997 alumnus of GIM with extensive experience at eBay."
        ),
        SuccessStoryItem.Story(
            "Shilpa Gulati (Corporate Leadership)",
            "An alumna of the Goa Institute of Management (GIM) (1996-98 batch). She was the first Indian woman CFO at the global retail giant IKEA and specializes in business operations and strategy."
        ),
        SuccessStoryItem.Story(
            "Raul Rebello (Banking/Finance)",
            "Alumnus of the Goa Institute of Management (GIM). He is the Managing Director & CEO of Mahindra Finance, leading one of India's top rural NBFCs."
        ),
        SuccessStoryItem.Story(
            "Sameer Batra (Corporate Leadership)",
            "Completed his PGDM in Marketing from GIM (1998-2000) and went on to become the CEO of Wynk Ltd, a startup owned by Bharti Airtel."
        ),
        SuccessStoryItem.Story(
            "Sachin Lawande (Technology/Corporate)",
            "A native of Taleigao, he is a 1988 graduate of Goa Engineering College (GEC). He is the President and CEO of Visteon Corporation in Michigan, a Fortune 500 company."
        ),
        SuccessStoryItem.Story(
            "Pankaj Ramani (Technology)",
            "A prominent alumnus of Goa Engineering College (GEC), currently serving as Vice President and Global Head of Enterprise Application & AI at Mphasis."
        ),

        SuccessStoryItem.Header("Sports & Arts", listOf("Arts")),
        SuccessStoryItem.Story(
            "Shikha Pandey",
            "An Indian international cricketer and a key member of the national women's team. She is a graduate of Goa Engineering College (GEC)."
        ),
        SuccessStoryItem.Story(
            "Anish Sood",
            "A prominent DJ, songwriter, and EDM producer who studied at Goa Engineering College."
        ),
        SuccessStoryItem.Story(
            "Cecille Rodrigues",
            "A well-known dancer and runner-up of DID Super Moms. She is an alumna of Carmel College for Women."
        ),
        SuccessStoryItem.Story(
            "Assavri Kulkarni",
            "A leading photographer in Goa's artistic community who pursued her passion for fine arts at the Goa College of Art."
        ),
        SuccessStoryItem.Story(
            "Dr. Suresh Amonkar (Education)",
            "A respected educationist, former chairman of the Goa Board of Secondary and Higher Secondary Education, and recipient of the highest civilian award from the Goa government for his contribution to literature and education."
        ),
        SuccessStoryItem.Story(
            "Gajanan Rama Parab (Academics)",
            "A distinguished alumnus of St. Xavier's College, Mapusa, who has excelled in the field of Mathematics and Computer Science, completing advanced internships at IIT Goa."
        ),

        SuccessStoryItem.Header("Literature & Academics", listOf("Arts")),
        SuccessStoryItem.Story(
            "Olivinho Gomes",
            "An eminent Konkani scholar and former acting Vice-Chancellor of Goa University."
        ),
        SuccessStoryItem.Story(
            "Nishtha Desai",
            "A respected scholar and author who completed her higher studies in Goa."
        ),
        SuccessStoryItem.Story(
            "Dr. Suresh Amonkar",
            "A distinguished educationist and translator who served as the chairman of the Goa Board of Secondary & Higher Secondary Education."
        ),
        SuccessStoryItem.Story(
            "Damodar Mauzo (Literature)",
            "The Jnanpith Award-winning Konkani author is a prominent figure who studied in Goa and has contributed immensely to literature and critical writing."
        )
    )
}
