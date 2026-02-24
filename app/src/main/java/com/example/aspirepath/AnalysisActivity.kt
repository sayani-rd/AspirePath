package com.example.aspirepath

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aspirepath.adapter.TrendingJobsAdapter
import com.example.aspirepath.models.TrendingJobItem
import com.example.aspirepath.utils.UserProfileHelper

class AnalysisActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)

        // Set up toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Trending Jobs"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val rvTrendingJobs = findViewById<RecyclerView>(R.id.rvTrendingJobs)
        rvTrendingJobs.layoutManager = LinearLayoutManager(this)

        UserProfileHelper.fetch {
            val allJobs = getTrendingJobs()
            val filtered = filterByStream(allJobs, UserProfileHelper.stream, UserProfileHelper.eligibility)
            rvTrendingJobs.adapter = TrendingJobsAdapter(filtered)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Keep only sections whose streams list contains the user's stream.
     * If the user is "10th Completed" (no stream) or "Other", show everything.
     */
    private fun filterByStream(
        items: List<TrendingJobItem>,
        stream: String,
        eligibility: String
    ): List<TrendingJobItem> {
        // 10th Completed has no stream — show all; "Other" or blank → show all
        if (eligibility == "10th Completed" || stream.isBlank() || stream.equals("Other", true)) {
            return items
        }

        val result = mutableListOf<TrendingJobItem>()
        var includeSection = false

        for (item in items) {
            when (item) {
                is TrendingJobItem.Header -> {
                    includeSection = item.streams.isEmpty() ||
                            item.streams.any { it.equals(stream, true) }
                    if (includeSection) result.add(item)
                }
                is TrendingJobItem.Job -> {
                    if (includeSection) result.add(item)
                }
            }
        }
        return result
    }

    private fun getTrendingJobs(): List<TrendingJobItem> {
        val list = mutableListOf<TrendingJobItem>()

        // 1. Technology & Engineering
        list.add(TrendingJobItem.Header("Technology & Engineering (Highest Growth)", listOf("Science")))
        list.add(TrendingJobItem.Job(
            "AI & Machine Learning Engineer",
            "They build the \"brains\" behind automation. They develop and deploy AI models (like ChatGPT-style LLMs or image recognition systems) and create data pipelines that allow these models to learn from information.",
            "Python, PyTorch/TensorFlow, Deep Learning, and Generative AI (LLMs).",
            "Degree: B.S./M.S. in Computer Science, Data Science, or Mathematics. A PhD is often preferred for high-level research roles.",
            "Professional certificates in Machine Learning (e.g., from Google, IBM, or AWS)."
        ))
        list.add(TrendingJobItem.Job(
            "Data Scientist & Data Analyst",
            "Analysts focus on interpreting historical data to answer specific business questions, while Scientists use advanced statistics and coding to predict future trends.",
            "SQL, Python/R, Tableau/PowerBI, and Statistical Modeling.",
            "Degree: Degree in Statistics, Economics, Computer Science, or Data Analytics.",
            "Google Data Analytics Professional Certificate or Microsoft Certified: Data Analyst Associate."
        ))
        list.add(TrendingJobItem.Job(
            "Cybersecurity Specialist / InfoSec Analyst",
            "They act as digital bodyguards. They monitor networks for breaches, perform \"ethical hacking\" to find vulnerabilities, and create security protocols to protect sensitive data.",
            "Network security, Incident Response, Cryptography, and Cloud Security.",
            "Degree: B.S. in Cybersecurity, IT, or Computer Science.",
            "CompTIA Security+, CISSP (for seniors), or CEH (Certified Ethical Hacker)."
        ))
        list.add(TrendingJobItem.Job(
            "Software / Full Stack Developer",
            "They build the apps and websites we use daily. A Full Stack developer handles both the \"Front-end\" (what you see) and the \"Back-end\" (the server and database logic).",
            "HTML/CSS, JavaScript (React/Node.js), Python, and Database management (PostgreSQL/MongoDB).",
            "Degree: B.S. in Computer Science or Software Engineering.\nAlternative: Coding Bootcamps are widely accepted if backed by a strong GitHub portfolio.",
            null
        ))
        list.add(TrendingJobItem.Job(
            "Cloud Computing & DevOps Engineer",
            "They manage the \"invisible\" infrastructure where software lives. DevOps engineers specifically bridge the gap between developers and IT operations to ensure software updates are released smoothly and automatically.",
            "AWS/Azure/GCP, Docker, Kubernetes, and Linux.",
            "Degree: B.S. in IT, Systems Engineering, or Computer Science.",
            "AWS Solutions Architect or Google Professional Cloud DevOps Engineer."
        ))

        // 2. Business, Finance & Management
        list.add(TrendingJobItem.Header("Business, Finance & Management", listOf("Commerce")))
        list.add(TrendingJobItem.Job(
            "Fintech Specialists & Blockchain Developers",
            "They build the future of money. Fintech Specialists manage digital payment systems and risk models, while Blockchain Developers create decentralized apps (dApps) and secure smart contracts.",
            "Solidity (for Ethereum), Python, Cryptography, and Smart Contract Auditing.",
            "Degree: B.S. in Computer Science or Financial Engineering.",
            "Certified Blockchain Developer (CBD) or specialized Fintech MBAs."
        ))
        list.add(TrendingJobItem.Job(
            "Financial Analysts & ESG Compliance Officers",
            "Financial Analysts use data to guide investments, but ESG Officers are the rising stars. They ensure a company meets \"Environmental, Social, and Governance\" standards.",
            "ESG Reporting Frameworks (GRI, SASB), Financial Modeling, and Carbon Accounting.",
            "Degree: B.S. in Finance, Economics, or Environmental Science.",
            "CFA (Chartered Financial Analyst) or the EFFAS Certified ESG Analyst (CESGA)."
        ))
        list.add(TrendingJobItem.Job(
            "Digital Product Managers",
            "They are the \"mini-CEOs\" of a specific digital product (like a banking app or a SaaS platform). They sit at the center of Design, Tech, and Business, defining the product vision.",
            "Agile/Scrum methodology, User Research (UX), and Market Strategy.",
            "Degree: Bachelor's in Business, Computer Science, or Information Systems.",
            "PMP (Project Management Professional) or Certified Scrum Master (CSM)."
        ))

        // 3. Healthcare & Life Sciences
        list.add(TrendingJobItem.Header("Healthcare & Life Sciences", listOf("Science")))
        list.add(TrendingJobItem.Job(
            "Nurses & Healthcare Technicians",
            "Nurses provide direct patient care, while technicians operate specialized equipment. Many nurses are moving into Nurse Practitioner (NP) roles to prescribe medications.",
            "Patient monitoring, emergency response, and digital health record management.",
            "Nursing: B.Sc. in Nursing (4 years). For advanced roles, a Master of Science in Nursing (MSN).",
            "Must pass a national licensure exam (like the NCLEX-RN)."
        ))
        list.add(TrendingJobItem.Job(
            "Doctors & Specialists",
            "They diagnose and treat complex medical conditions. Surgeons and Anesthesiologists are in high demand. Modern specialists are trained in Robotic Surgery and Precision Medicine.",
            "Advanced diagnosis, surgical precision, and interdisciplinary collaboration.",
            "Foundation: A Bachelor's degree (Pre-med/MBBS) followed by a Medical Degree (MD/DO). Specialization: 3-7 years Residency.",
            "Board certification in their specific specialty is mandatory."
        ))
        list.add(TrendingJobItem.Job(
            "Mental Health Professionals & Therapists",
            "They provide emotional and psychological support. Includes Psychiatrists (medicine) and Psychologists/Counselors (talk therapy). Huge demand for Tele-therapy.",
            "Active listening, Cognitive Behavioral Therapy (CBT), and crisis intervention.",
            "Psychiatrist: MD + Residency. Psychologist: Ph.D./Psy.D. Counselor: Master's degree.",
            "State or national licensure is strictly required."
        ))

        // 4. Creative, Communication & Social Sciences
        list.add(TrendingJobItem.Header("Creative, Communication & Social Sciences", listOf("Arts")))
        list.add(TrendingJobItem.Job(
            "Digital Content Creators & Strategists",
            "They engineer engagement. Creators produce assets, Strategists use SEO and analytics to reach audiences. Heavily involves AI tools for content scaling.",
            "Short-form video editing (CapCut/Premiere), SEO/SEM, UX principles, and AI-driven content tools.",
            "Degree: Bachelor's in Marketing, Communications, or Digital Media.",
            "Google Analytics 4, HubSpot Content Marketing, or Meta Blueprint. Portfolio is key."
        ))
        list.add(TrendingJobItem.Job(
            "Instructional Designers / EdTech Specialists",
            "They are the architects of online learning. They turn complex info into interactive digital courses and manage Learning Management Systems.",
            "The ADDIE model, e-learning authoring tools (Articulate Storyline), and UX for learning.",
            "Degree: Bachelor's or Master's in Instructional Design or Educational Technology.",
            "Certified Professional in Learning and Performance (CPLP) or Google Certified Educator."
        ))

        // 5. Emerging & Future-Focused Careers — shown to ALL streams
        list.add(TrendingJobItem.Header("Emerging & Future-Focused Careers", listOf("Science", "Commerce", "Arts")))
        list.add(TrendingJobItem.Job(
            "Quantum Computing & Advanced Tech Specialists",
            "They work with \"non-classical\" computers to solve complex problems. Focus is on Quantum Software Engineering—writing code for quantum hardware.",
            "Quantum mechanics, Linear Algebra, Python, and SDKs like Qiskit or Cirq.",
            "Degree: PhD in Physics, Math, or CS is gold standard. Entry: Bachelor's in CS/Physics + specialized Master's.",
            "Specialized badges from IBM Quantum or Microsoft Azure Quantum."
        ))
        list.add(TrendingJobItem.Job(
            "AI Ethics, AI Product Managers & Data Governance Leads",
            "The \"conscience\" and \"strategists\" of AI. They ensure models aren't biased, decide what AI to build, and manage massive datasets for privacy.",
            "Algorithmic bias auditing, EU AI Act compliance, Agile management, and Data Privacy laws.",
            "Degree: Bachelor's/Master's in Law, Philosophy, Business, or CS.",
            "Certified AI Governance Professional (AIGP) or IAPP Privacy certifications."
        ))
        list.add(TrendingJobItem.Job(
            "Sustainability & Climate Tech Analysts",
            "They help companies reach \"Net-Zero\". Analyze carbon footprints, evaluate green investment risks, and deploy Carbon Capture technologies.",
            "Carbon accounting, Life Cycle Assessment (LCA), ESG reporting, and GIS.",
            "Degree: B.S. in Environmental Science, Sustainable Engineering, or Economics.",
            "LEED, CC-P (Climate Change Professional), or SASB credential."
        ))
        list.add(TrendingJobItem.Job(
            "Space & Robotics Engineers",
            "They design machines that operate in extreme environments—from automated warehouses on Earth to rovers on Mars. This role merges Mechanical, Electrical, and Software engineering into one. In 2026, many are focused on \"Satellite Logistics\" and \"Autonomous Robotics.\"",
            "ROS (Robot Operating System), C++, CAD (SolidWorks), Avionics, and Kinematics.",
            "Degree: B.S. in Aerospace Engineering, Mechanical Engineering, or Mechatronics.\nAdvanced: A Master's is highly recommended for space-specific roles to specialize in areas like propulsion or orbital mechanics.",
            "Experience: Participation in robotics competitions (like VEX or FIRST) is a huge plus."
        ))

        return list
    }
}
