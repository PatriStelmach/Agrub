import {Language, type LibraryPlugin} from "@/types/types"

export const pluginLibraryData: LibraryPlugin[] = [
  {
    id: 1,
    name: "System Monitor",
    creator: "John Smith",
    language: ".py",
    description: "Monitors system resources including CPU, memory, and disk usage",
    weight: 5,
    createdAt: new Date("2024-01-15T10:30:00"),
    tags: ["monitoring", "system", "performance"]
  },
  {
    id: 2,
    name: "Log Analyzer",
    creator: "Sarah Johnson",
    language: Language.PYTHON,
    description: "Analyzes application logs for error patterns and anomalies",
    weight: 8,
    createdAt: new Date("2024-02-20T14:15:00"),
    tags: ["logging", "analysis", "debugging"]
  },
  {
    id: 3,
    name: "Network Scanner",
    creator: "Mike Chen",
    language: ".bash",
    description: "Scans network for available devices and open ports",
    weight: 3,
    createdAt: new Date("2024-03-10T09:45:00"),
    tags: ["network", "security", "scanning"]
  },
  {
    id: 4,
    name: "Database Backup",
    creator: "Emily Davis",
    language: ".ps1",
    description: "Automated database backup and recovery system",
    weight: 7,
    createdAt: new Date("2024-01-25T16:20:00"),
    tags: ["database", "backup", "automation"]
  },
  {
    id: 5,
    name: "API Rate Limiter",
    creator: "Alex Wilson",
    language: ".py",
    description: "Implements rate limiting for API endpoints",
    weight: 4,
    createdAt: new Date("2024-02-05T11:30:00"),
    tags: ["api", "security", "performance"]
  },
  {
    id: 6,
    name: "File Watcher",
    creator: "Lisa Anderson",
    language: ".bash",
    description: "Monitors file system changes and triggers actions",
    weight: 6,
    createdAt: new Date("2024-03-15T13:00:00"),
    tags: ["filesystem", "monitoring", "automation"]
  },
  {
    id: 7,
    name: "Email Notifier",
    creator: "David Brown",
    language: ".py",
    description: "Sends email notifications for system events",
    weight: 2,
    createdAt: new Date("2024-01-08T08:45:00"),
    tags: ["email", "notifications", "communication"]
  },
  {
    id: 8,
    name: "Cache Manager",
    creator: "Jennifer Lee",
    language: ".ps1",
    description: "Manages application cache with intelligent eviction policies",
    weight: 9,
    createdAt: new Date("2024-02-28T15:30:00"),
    tags: ["cache", "performance", "optimization"]
  },
  {
    id: 9,
    name: "SSL Certificate Monitor",
    creator: "Robert Taylor",
    language: ".bash",
    description: "Monitors SSL certificate expiration dates",
    weight: 1,
    createdAt: new Date("2024-01-12T07:15:00"),
    tags: ["ssl", "security", "monitoring"]
  },
  {
    id: 10,
    name: "Message Queue Processor",
    creator: "Maria Garcia",
    language: ".py",
    description: "Processes messages from message queues efficiently",
    weight: 8,
    createdAt: new Date("2024-03-05T12:20:00"),
    tags: ["queue", "messaging", "async"]
  },
  {
    id: 11,
    name: "Metrics Collector",
    creator: "James Miller",
    language: ".ps1",
    description: "Collects and aggregates application metrics",
    weight: 5,
    createdAt: new Date("2024-02-12T10:00:00"),
    tags: ["metrics", "monitoring", "analytics"]
  },
  {
    id: 12,
    name: "Configuration Validator",
    creator: "Patricia Martinez",
    language: ".py",
    description: "Validates configuration files against schemas",
    weight: 3,
    createdAt: new Date("2024-01-22T14:45:00"),
    tags: ["configuration", "validation", "tools"]
  },
  {
    id: 13,
    name: "Session Manager",
    creator: "Christopher White",
    language: ".bash",
    description: "Manages user sessions and authentication tokens",
    weight: 6,
    createdAt: new Date("2024-02-18T09:30:00"),
    tags: ["session", "authentication", "security"]
  },
  {
    id: 14,
    name: "Data Transformer",
    creator: "Nancy Thomas",
    language: ".py",
    description: "Transforms data between different formats",
    weight: 4,
    createdAt: new Date("2024-03-08T16:10:00"),
    tags: ["data", "transformation", "conversion"]
  },
  {
    id: 15,
    name: "Memory Leak Detector",
    creator: "Daniel Jackson",
    language: ".ps1",
    description: "Detects memory leaks in applications",
    weight: 7,
    createdAt: new Date("2024-01-30T11:55:00"),
    tags: ["memory", "debugging", "performance"]
  },
  {
    id: 16,
    name: "WebSocket Server",
    creator: "Laura Martin",
    language: ".py",
    description: "WebSocket server for real-time communication",
    weight: 6,
    createdAt: new Date("2024-02-25T13:40:00"),
    tags: ["websocket", "realtime", "communication"]
  },
  {
    id: 17,
    name: "Image Processor",
    creator: "Matthew Harris",
    language: ".py",
    description: "Processes and optimizes images automatically",
    weight: 8,
    createdAt: new Date("2024-03-12T08:25:00"),
    tags: ["image", "processing", "optimization"]
  },
  {
    id: 18,
    name: "DNS Resolver",
    creator: "Barbara Clark",
    language: ".bash",
    description: "Custom DNS resolver with caching capabilities",
    weight: 3,
    createdAt: new Date("2024-01-18T15:15:00"),
    tags: ["dns", "network", "caching"]
  },
  {
    id: 19,
    name: "Task Scheduler",
    creator: "Joseph Lewis",
    language: ".ps1",
    description: "Advanced task scheduling with cron-like functionality",
    weight: 5,
    createdAt: new Date("2024-02-08T10:50:00"),
    tags: ["scheduling", "automation", "tasks"]
  },
  {
    id: 20,
    name: "Compression Utility",
    creator: "Susan Walker",
    language: ".py",
    description: "High-performance file compression utility",
    weight: 9,
    createdAt: new Date("2024-03-18T14:30:00"),
    tags: ["compression", "utility", "performance"]
  },
  {
    id: 21,
    name: "Authentication Middleware",
    creator: "Kevin Hall",
    language: ".bash",
    description: "Middleware for handling authentication in web apps",
    weight: 6,
    createdAt: new Date("2024-01-28T09:20:00"),
    tags: ["authentication", "middleware", "security"]
  },
  {
    id: 22,
    name: "PDF Generator",
    creator: "Michelle Allen",
    language: ".ps1",
    description: "Generates PDF documents from templates",
    weight: 7,
    createdAt: new Date("2024-02-22T16:45:00"),
    tags: ["pdf", "generation", "documents"]
  },
  {
    id: 23,
    name: "Health Check Service",
    creator: "Steven Young",
    language: ".py",
    description: "Service health monitoring with automatic alerts",
    weight: 4,
    createdAt: new Date("2024-03-02T11:10:00"),
    tags: ["health", "monitoring", "alerts"]
  },
  {
    id: 24,
    name: "Throttling Service",
    creator: "Helen King",
    language: ".py",
    description: "Request throttling service for API protection",
    weight: 5,
    createdAt: new Date("2024-01-25T13:35:00"),
    tags: ["throttling", "api", "protection"]
  },
  {
    id: 25,
    name: "Encryption Helper",
    creator: "Paul Wright",
    language: ".bash",
    description: "Utility for encrypting and decrypting data",
    weight: 8,
    createdAt: new Date("2024-02-15T10:25:00"),
    tags: ["encryption", "security", "utility"]
  },
  {
    id: 26,
    name: "File Upload Handler",
    creator: "Karen Lopez",
    language: ".ps1",
    description: "Secure file upload handler with validation",
    weight: 6,
    createdAt: new Date("2024-03-20T15:50:00"),
    tags: ["upload", "files", "security"]
  },
  {
    id: 27,
    name: "Cache Warmer",
    creator: "Thomas Hill",
    language: ".py",
    description: "Pre-warms cache with frequently accessed data",
    weight: 3,
    createdAt: new Date("2024-01-14T08:40:00"),
    tags: ["cache", "performance", "optimization"]
  },
  {
    id: 28,
    name: "API Documentation Generator",
    creator: "Betty Scott",
    language: ".bash",
    description: "Generates API documentation from code annotations",
    weight: 5,
    createdAt: new Date("2024-02-27T12:15:00"),
    tags: ["documentation", "api", "generation"]
  },
  {
    id: 29,
    name: "Connection Pool Manager",
    creator: "Ronald Green",
    language: ".py",
    description: "Manages database connection pools efficiently",
    weight: 7,
    createdAt: new Date("2024-03-01T09:55:00"),
    tags: ["database", "connection", "pooling"]
  },
  {
    id: 30,
    name: "Event Logger",
    creator: "Carol Adams",
    language: ".ps1",
    description: "High-performance event logging system",
    weight: 8,
    createdAt: new Date("2024-02-14T17:20:00"),
    tags: ["logging", "events", "performance"]
  }
]
