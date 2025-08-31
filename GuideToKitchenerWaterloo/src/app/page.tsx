"use client";

import { useState, useMemo } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { Checkbox } from "@/components/ui/checkbox";
import { cn } from "@/lib/utils";


const TAG_COLORS: Record<string, string> = {
  // Pharmacies & Groceries
  "Open 24-hours": "bg-green-600 text-white",
  "Closes < 9 pm": "bg-orange-500 text-white",
  "Proximity": "bg-blue-600 text-white",
  "Ratings ≥ 4": "bg-purple-600 text-white",
  "How busy right now": "bg-yellow-600 text-black",
  "Closing < 9 pm": "bg-orange-400 text-black",

  // Hospitals
  "Walk-in": "bg-blue-800 text-white",
  "Urgent Care": "bg-red-600 text-white",
  "Avg Wait < 30 min": "bg-teal-600 text-white",

  // Barbers
  "Average Haircut Cost": "bg-gray-500 text-white",
  "Same-day Appointments": "bg-indigo-500 text-white",

  // Public Transportation
  "Bus": "bg-green-800 text-white",
  "Train": "bg-gray-800 text-white",
  "Taxi Services": "bg-yellow-700 text-black",

  // Dental Clinics
  "Average Cost": "bg-pink-500 text-white",
  "Wait Times < 20 min": "bg-cyan-600 text-white",
  "Kids Accepted": "bg-teal-400 text-black",

  // Parks
  "Playground": "bg-orange-300 text-black",
  "Basketball Court": "bg-yellow-500 text-black",
  "Field": "bg-green-400 text-black",
  "Dog-friendly": "bg-lime-500 text-black",

  // Mobile Carriers
  "Supports Rogers": "bg-red-700 text-white",
  "Supports Bell": "bg-blue-400 text-white",
  "Supports Telus": "bg-green-600 text-white",
};

type Venue = {
  id: number;
  name: string;
  tags: string[];
  image: string; // required
};

const CATEGORY_DEFS = {
  Pharmacies: {
    tags: [
      "Open 24-hours",
      "Closes < 9 pm",
      "Proximity",
      "Ratings ≥ 4",
    ],
    venues: [
      { id: 1, name: "Pharmasave Westmount Place Pharmacy & Home Health Care", tags: ["Open 24-hours", "Proximity", "Ratings ≥ 4"], image: "/images/pharmasave_westmount.webp" },
      { id: 2, name: "Frederick Mall Pharmacy", tags: ["Closes < 9 pm"], image: "/images/frederick.webp"},
      { id: 3, name: "Shoppers Drug Mart", tags: ["Open 24-hours"], image: "/images/shoppers.webp" },
      { id: 4, name: "Queens Pharmacy", tags: ["Proximity", "Ratings ≥ 4"], image: "/images/queens.webp" },
      { id: 5, name: "Ottawa & River IDA Pharmacy", tags: ["Closes < 9 pm", "Proximity"], image: "/images/ottawa.webp" },
      { id: 6, name: "Pharmasave Riepert", tags: ["Open 24-hours", "Ratings ≥ 4"], image: "/images/riepert.webp" },
      { id: 7, name: "Spark Pharmacy", tags: ["Ratings ≥ 4"], image: "/images/spark.webp" },
      { id: 8, name: "Pharmasave Country Hills", tags: ["Proximity"], image: "/images/countryhills.webp" },
      { id: 9, name: "Deer Ridge Pharmacy", tags: ["Closes < 9 pm", "Open 24-hours"], image: "/images/deer_ridge.webp" },
      { id: 10, name: "Apothecare Pharmacy", tags: ["Closes < 9 pm", "Open 24-hours"], image: "/images/apothecare.webp" },
    ],
  },

  Hospitals: {
    tags: [
      "Walk-in",
      "Urgent Care",
      "Avg Wait < 30 min",
      "Proximity",
    ],
    venues: [
      { id: 1, name: "WRHN @ Midtown", tags: ["Open 24-hours", "Ratings ≥ 4"], image: "/images/midtown.webp" },
      { id: 2, name: "WRHN @ Queens Blvd", tags: ["Open 24-hours"], image: "/images/queensblvd.webp"},
      { id: 3, name: "WRHN @ Chicopee", tags: ["Open 24-hours"], image: "/images/chicopee.webp" },
    ],
  },

  Barbers: {
    tags: [
      "Average Cost",
      "Same-day Appointments",
      "Proximity",
      "Ratings ≥ 4",
    ],
    venues: Array.from({ length: 9 }, (_, i) => ({
      id: 200 + i,
      name: `Barber Shop ${i + 1}`,
      tags: [
        ...(i % 2 === 0 ? ["Same-day Appointments"] : ["Average Haircut Cost"]),
        ...(i % 3 === 0 ? ["Proximity"] : []),
        ...(i % 2 === 1 ? ["Ratings ≥ 4"] : []),
      ],
      image: "/images/yourimage.jpg",
    })),
  },

  "Public Transportation": {
    tags: [
      "Bus",
      "Train",
      "Taxi Services",
      "Proximity",
    ],
    venues: Array.from({ length: 9 }, (_, i) => ({
      id: 300 + i,
      name: `Transit Stop ${i + 1}`,
      tags: [
        ...(i % 2 === 0 ? ["Bus"] : ["Train"]),
        ...(i % 3 === 0 ? ["Taxi Services"] : []),
        ...(i % 2 === 1 ? ["Proximity"] : []),
      ],
      image: "/images/yourimage.jpg",
    })),
  },

  "Dental Clinics": {
    tags: [
      "Average Cost",
      "Wait Times < 20 min",
      "Kids Accepted",
      "Proximity",
      "Ratings ≥ 4",
    ],
    venues: Array.from({ length: 9 }, (_, i) => ({
      id: 400 + i,
      name: `Dental Clinic ${i + 1}`,
      tags: [
        ...(i % 2 === 0 ? ["Average Cost"] : ["Wait Times < 20 min"]),
        ...(i % 3 === 0 ? ["Kids Accepted"] : []),
        ...(i % 2 === 1 ? ["Proximity"] : []),
        ...(i % 2 === 0 ? ["Ratings ≥ 4"] : []),
      ],
      image: "/images/yourimage.jpg",
    })),
  },

  Parks: {
    tags: [
      "Playground",
      "Basketball Court",
      "Field",
      "Dog-friendly",
      "Proximity",
    ],
    venues: Array.from({ length: 9 }, (_, i) => ({
      id: 500 + i,
      name: `Park ${i + 1}`,
      tags: [
        ...(i % 2 === 0 ? ["Playground"] : ["Basketball Court"]),
        ...(i % 3 === 0 ? ["Field"] : []),
        ...(i % 2 === 1 ? ["Dog-friendly"] : []),
        ...(i % 2 === 0 ? ["Proximity"] : []),
      ],
      image: "/images/yourimage.jpg",
    })),
  },

  "Grocery Stores": {
    tags: [
      "Open 24-hours",
      "How busy right now",
      "Closing < 9 pm",
      "Proximity",
      "Ratings ≥ 4",
    ],
    venues: Array.from({ length: 9 }, (_, i) => ({
      id: 600 + i,
      name: `Grocery Store ${i + 1}`,
      tags: [
        ...(i % 2 === 0 ? ["Open 24-hours"] : ["Closing < 9 pm"]),
        ...(i % 3 === 0 ? ["How busy right now"] : []),
        ...(i % 2 === 1 ? ["Proximity"] : []),
        ...(i % 2 === 0 ? ["Ratings ≥ 4"] : []),
      ],
      image: "/images/yourimage.jpg",
    })),
  },

  "Mobile-Carrier Shops": {
    tags: [
      "Supports Rogers",
      "Supports Bell",
      "Supports Telus",
      "Proximity",
      "Ratings ≥ 4",
    ],
    venues: Array.from({ length: 9 }, (_, i) => ({
      id: 700 + i,
      name: `Carrier Shop ${i + 1}`,
      tags: [
        ...(i % 3 === 0 ? ["Supports Rogers"] : []),
        ...(i % 3 === 1 ? ["Supports Bell"] : []),
        ...(i % 3 === 2 ? ["Supports Telus"] : []),
        ...(i % 2 === 0 ? ["Proximity"] : []),
        ...(i % 2 === 1 ? ["Ratings ≥ 4"] : []),
      ],
      image: "/images/yourimage.jpg",
    })),
  },
};

type CategoryKey = keyof typeof CATEGORY_DEFS;

export default function GuideToKWApp() {
  const [activeCat, setActiveCat] = useState<CategoryKey>("Pharmacies");
  const [selectedTags, setSelectedTags] = useState<string[]>([]);
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const venues = useMemo(() => {
    const { venues } = CATEGORY_DEFS[activeCat];
    if (selectedTags.length === 0) return venues;
    return venues.filter((v) => selectedTags.every((t) => v.tags.includes(t)));
  }, [activeCat, selectedTags]);

  function toggleTag(tag: string) {
    setSelectedTags((prev) =>
      prev.includes(tag) ? prev.filter((t) => t !== tag) : [...prev, tag]
    );
  }

  function SidebarContent() {
    return (
      <aside className="w-56 shrink-0 border-r border-[#FFD700] bg-black text-[#FFD700] h-full z-20 top-0 left-0 transition-transform duration-200">
        <div className="px-4 py-2 font-bold text-lg tracking-wide text-[#FFD700]">Menu</div>
        <ul className="py-2">
          {Object.keys(CATEGORY_DEFS).map((k) => (
            <li key={k}>
              <button
                onClick={() => {
                  setActiveCat(k as CategoryKey);
                  setSelectedTags([]);
                  setSidebarOpen(false); // Auto-close on mobile
                }}
                className={cn(
                  "w-full text-left px-4 py-2 hover:bg-[#FFD700] hover:text-black transition",
                  k === activeCat &&
                    "font-semibold bg-[#FFD700] text-black border-l-4 border-[#FFD700]"
                )}
              >
                {k}
              </button>
            </li>
          ))}
        </ul>
      </aside>
    );
  }

  return (
    <div className="min-h-screen flex flex-col bg-[#FFD700] relative">
      {/* Header */}
      <header className="w-full z-30 bg-black text-[#FFD700] py-3 px-6 flex items-center justify-center shadow">
        <h1 className="text-2xl font-extrabold tracking-wide">GuideToKW</h1>
      </header>

      {/* App Content: Sidebar + Main */}
      <div className="flex flex-1 min-h-0 relative">
        {/* Hamburger icon (mobile only) */}
        <button
          className="sm:hidden fixed top-20 left-4 z-30 bg-black rounded p-2 border border-[#FFD700]"
          onClick={() => setSidebarOpen((v) => !v)}
          aria-label="Open Menu"
        >
          <svg width={28} height={28} fill="none" viewBox="0 0 24 24">
            <rect y={5} width={24} height={2} rx={1} fill="#FFD700" />
            <rect y={11} width={24} height={2} rx={1} fill="#FFD700" />
            <rect y={17} width={24} height={2} rx={1} fill="#FFD700" />
          </svg>
        </button>

        {/* Sidebar (always visible on desktop, toggled on mobile) */}
        <div>
          {/* Desktop sidebar */}
          <div className="hidden sm:block h-full">
            <SidebarContent />
          </div>
          {/* Mobile sidebar */}
          {sidebarOpen && (
            <div className="block sm:hidden fixed top-0 left-0 h-full z-30">
              <SidebarContent />
            </div>
          )}
        </div>

        {/* Overlay for mobile when menu open */}
        {sidebarOpen && (
          <div
            className="fixed inset-0 bg-black bg-opacity-40 z-20 sm:hidden"
            onClick={() => setSidebarOpen(false)}
          />
        )}

        {/* Main panel */}
        <main className="flex-1 p-4 grid gap-4 gap-y-2 lg:grid-cols-3 md:grid-cols-2 sm:grid-cols-1 bg-[#FFD700] min-h-screen">
          {/* Filter pane */}
          <section className="col-span-full">
            <h2 className="text-lg font-semibold mb-2 text-black">Filters</h2>
            <div className="flex flex-wrap gap-4">
              {CATEGORY_DEFS[activeCat].tags.map((tag) => (
                <label
                  key={tag}
                  className={cn(
                    "inline-flex items-center gap-2 px-3 py-1 rounded-full shadow-sm border cursor-pointer text-xs font-semibold",
                    TAG_COLORS[tag] || "bg-black text-white border-white"
                  )}
                >
                  <Checkbox
                    checked={selectedTags.includes(tag)}
                    onCheckedChange={() => toggleTag(tag)}
                    className="accent-white"
                  />
                  {tag}
                </label>
              ))}
            </div>
          </section>

          {/* Venue cards */}
          {venues.map((v) => (
            <Card
              key={v.id}
              className="cursor-pointer bg-white border border-[#FFD700] rounded-xl shadow-md hover:bg-[#FFD700] hover:text-black transition"
              onClick={() => alert(`TODO: open modal for ${v.name}`)}
            >
              <CardContent className="p-4">
                <h3 className="font-bold mb-1 text-black">{v.name}</h3>
                <div className="flex flex-wrap gap-1 mt-2">
                  {v.tags.map((tag) => (
                    <span
                      key={tag}
                      className={`px-2 py-0.5 rounded-full text-xs font-semibold ${TAG_COLORS[tag] || "bg-gray-300 text-black"}`}
                    >
                      {tag}
                    </span>
                  ))}
                </div>

                {v.image && (
                  <img
                    src={v.image}
                    alt={v.name}
                    className="w-full h-96 object-cover rounded-lg mb-2"
                  />
                )}
              </CardContent>
            </Card>
          ))}
        </main>
      </div>
    </div>
  );
}
