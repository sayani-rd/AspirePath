package com.example.aspirepath

data class ShippingInstitute(
    val id: Int,
    val name: String,
    val description: String,
    val website: String,
    val location: String,
    val courses: List<String>,
    val qualifications: List<String>,
    val ageRequirement: List<String>
)

object ShippingInstituteData {
    val institutes = listOf(
        ShippingInstitute(
            id = 1,
            name = "NUSI Maritime Academy (NMA)",
            description = "NUSI Maritime Academy is a non-profit academy specifically for training Ratings (crew responsible for practical ship operations).",
            website = "https://nusiacademy.edu.in",
            location = "Sucaldem, Chinchinim, Salcete, Goa",
            courses = listOf(
                "GP Rating (General Purpose Rating – 6 months)",
                "CCMC (Certificate Course in Maritime Catering – 6 months)"
            ),
            qualifications = listOf(
                "GP Rating: 10th Standard pass, Min 40% overall, Min 40% in Maths, Science, and English",
                "CCMC: 10th Standard pass, 40% in English (50% if from a vernacular medium)"
            ),
            ageRequirement = listOf(
                "18 to 25 years (on the date of course commencement)"
            )
        ),
        ShippingInstitute(
            id = 2,
            name = "Institute of Maritime Studies (IMS)",
            description = "IMS is a premier institute in Goa for engineering students aspiring to join the Merchant Navy as officers.",
            website = "https://imsgoa.org",
            location = "ISBT Complex, Bogda, Vasco da Gama, Goa",
            courses = listOf(
                "GME (Graduate Marine Engineering – 1 year)",
                "DME (Diploma in Marine Engineering – 2 years)"
            ),
            qualifications = listOf(
                "GME: B.E./B.Tech in Mechanical Engineering or Naval Architecture, Min 50% marks",
                "DME: 3-year Diploma in Mechanical, Electrical, or Shipbuilding Engineering, Min 50% marks",
                "English Requirement: Min 50% marks in English at 10th, 12th, or Degree/Diploma level"
            ),
            ageRequirement = listOf(
                "Up to 28 years",
                "Relaxation for SC/ST and women as per Government norms"
            )
        ),
        ShippingInstitute(
            id = 3,
            name = "Sea Scan Maritime Foundation",
            description = "Sea Scan Maritime Foundation provides pre-sea training for ratings and mandatory safety certifications for active seafarers.",
            website = "https://seascanmaritime.in",
            location = "Verna Industrial Estate (Phase II) & Chicalim, Goa",
            courses = listOf(
                "GP Rating (Pre-sea)",
                "CCMC (Pre-sea)",
                "STCW Courses: BST, STSDSD, Refresher Courses"
            ),
            qualifications = listOf(
                "10th Standard pass with minimum 40% marks",
                "English, Maths, and Science required for GP Rating"
            ),
            ageRequirement = listOf(
                "17.5 to 25 years for pre-sea courses",
                "18 years and above for STCW modular courses"
            )
        ),
        ShippingInstitute(
            id = 4,
            name = "Kamaxi Maritime Academy",
            description = "Kamaxi Maritime Academy specializes in safety training and preparation for the cruise ship industry.",
            website = "https://kamaximaritime.com",
            location = "Utility Plot No. 1, Phase 1A, Verna Industrial Estate, Verna",
            courses = listOf(
                "Basic STCW Safety Training (12–14 days)",
                "Passenger / Cruise Ship Safety (Crowd Management)",
                "Refresher Training: RPST, RFPFF"
            ),
            qualifications = listOf(
                "SSC (10th) Passing Certificate",
                "Valid Indian Passport"
            ),
            ageRequirement = listOf(
                "18 years and above"
            )
        ),
        ShippingInstitute(
            id = 5,
            name = "Maritime School (Captain of Ports)",
            description = "A government-run institute for individuals aiming to work on local barges, ferries, and river cruises in Goa.",
            website = "https://ports.goa.gov.in",
            location = "Britona, Bardez, Goa",
            courses = listOf(
                "New Entrants Training (Deck & Engine Room – 4 months)",
                "Advanced Refresher Course (for Inland Vessel Certificates)"
            ),
            qualifications = listOf(
                "7th Standard pass and above",
                "Must know how to swim"
            ),
            ageRequirement = listOf(
                "18 years and above for new entrants",
                "25 years and above for advanced refresher courses"
            )
        )
    )
}
