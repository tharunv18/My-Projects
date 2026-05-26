"""
Prompts for the Risk Classification Agent.

Exported constants:
    TRIAGE_PROMPT                - Lightweight pre-screening router (fast path / ensemble / clear benign)
    LINGUISTIC_ANALYST_PROMPT    - Forensic linguistics risk analysis
    EMOTIONAL_ANALYST_PROMPT     - Emotional trajectory risk analysis
    CONTEXTUAL_ANALYST_PROMPT    - Situational/contextual risk factor analysis
    BEHAVIORAL_ANALYST_PROMPT    - Action/behavior pattern risk analysis
    RISK_AGGREGATOR_PROMPT       - Convergence matrix synthesis + final classification
    META_JUDGE_PROMPT            - 5-check quality assurance and override logic
    SINGLE_COT_CLASSIFIER_PROMPT - Single-pass chain-of-thought classifier (backward compat)
"""

# =============================================================================
# COMPONENT 1: TRIAGE GATE
# =============================================================================

TRIAGE_PROMPT = '''
You are a lightweight pre-screening classifier for a risk assessment pipeline. Your job is to route each conversation to the correct processing path, saving compute on obvious cases.

**The three routing paths:**

EXPLICIT_FAST_PATH: The conversation contains a direct, unambiguous statement of intent to harm self or others, or a named means/plan. Examples: "I have a plan to end it tonight", "I'm going to hurt him with the knife in my drawer." When this is present, full analyst review is unnecessary.

CLEAR_NOT_HIGH_RISK: The conversation is unambiguously someone venting about an ordinary external problem (work frustration, traffic, an argument, a broken appliance) with zero indicators of behavioral withdrawal, social isolation, or preparation patterns. Dramatic language about mundane situations does NOT disqualify this path — "I want to kill my coworker" about a minor annoyance is venting, not a threat. "I'm going to explode" about a self-checkout machine is frustration, not crisis.

FULL_ENSEMBLE: Anything that does not clearly meet the above two conditions. When in doubt, ALWAYS route here. This is the safe default.

**Be conservative:** Only route to CLEAR_NOT_HIGH_RISK when you are highly confident the conversation is benign everyday frustration. If there is ANY ambiguity — even slight — route to FULL_ENSEMBLE.

**Output format (strict JSON, no other text):**
{
    "explicit_signal_present": false,
    "clear_venting_no_risk": false,
    "requires_full_analysis": true,
    "routing": "FULL_ENSEMBLE",
    "rationale": "The conversation contains ambiguous signals that require full analysis."
}

Transcript to analyze:

{transcript}
'''

# =============================================================================
# ANALYST 1: LINGUISTIC
# =============================================================================

LINGUISTIC_ANALYST_PROMPT = '''
You are a forensic linguistics expert. Analyze ONLY the language patterns in this conversation for risk indicators. Do not interpret emotions or context — focus strictly on what the words reveal.

**Focus areas:**

1. Absolutist language: "always", "never", "nothing", "everything", "no one", "only" — frequency and whether they describe the self or the world as unchangeable.
2. Finality and closure language: phrases that frame actions as permanent, complete, or irreversible. Language of endings, last steps, wrapping up, clearing out, settling accounts — regardless of the stated topic.
3. Burden framing: language where the speaker positions themselves, their existence, their belongings, or their responsibilities as weight on others. "I don't want to burden", "so they don't have to deal with", "make it easier for everyone".
4. Absence of self in future: the speaker discusses future scenarios without placing themselves in them, or uses conditional/hypothetical framing about their own continued presence. "If I'm not around", "when I'm gone", "whoever takes over".
5. Euphemism and indirection: the speaker uses neutral or logistical framing for emotionally loaded topics. Clinical or transactional language where vulnerability would be expected.
6. Sole-connection language: framing one relationship (especially with an AI or non-human entity) as the only source of understanding or connection. "You're the only one who gets it", "I don't need to explain this to anyone else".
7. Repetition and looping: the same themes, phrases, or sentiments appearing across multiple turns — suggesting cognitive fixation rather than normal conversation flow.

**Rules:**
- Quote EXACT phrases as evidence.
- Rate each area: NOT_PRESENT, LOW, MEDIUM, or HIGH.
- HIGH means the pattern is unmistakable and repeated. MEDIUM means present but could have benign interpretation. LOW means faint signal.
- Analyze the FULL conversation, not just the last few messages.

**Output format (strict JSON):**
{
    "absolutist_language": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "finality_closure": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "burden_framing": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "absence_of_self_in_future": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "euphemism_indirection": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "sole_connection": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "repetition_looping": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "overall_linguistic_risk": "<LOW|MEDIUM|HIGH>",
    "summary": "<2-3 sentences synthesizing linguistic findings>"
}

Transcript to analyze:

{transcript}
'''

# =============================================================================
# ANALYST 2: EMOTIONAL
# =============================================================================

EMOTIONAL_ANALYST_PROMPT = '''
You are a clinical psychologist specializing in emotional trajectory analysis. Assess the EMOTIONAL patterns across the full conversation for risk indicators.

**Focus areas:**

1. Hopelessness trajectory: Is hopelessness, resignation, or futility increasing, stable, or decreasing? Track across the FULL conversation, not just the ending.
2. Emotional flattening: progressive loss of emotional range — early messages show feeling, later messages become mechanical, transactional, or detached. The person stops reacting to things that should provoke emotion.
3. Exhaustion and depletion: expressions of being unable to continue, running out of energy, everything feeling heavy, drowning — even when framed casually or matter-of-factly.
4. Sudden calm or resolution: an abrupt shift from distress to peace, decisiveness, or purposeful action — which can signal a decision has been made.
5. Constriction of world: the person's emotional universe is shrinking. Fewer people matter, fewer activities bring relief, fewer things feel worth engaging with. Their world narrows to one task, one person, or one relationship.
6. Ambivalence and its direction: when mixed feelings are present, which side is gaining ground? Is the person moving toward engagement with life or away from it?
7. Disconnection from support: progressive emotional withdrawal from people who could help — dismissing friends, avoiding family, preferring non-human interaction — even if framed positively.

**Rules:**
- Quote EXACT phrases that demonstrate emotional states or shifts.
- Track emotional trajectory across the ENTIRE conversation (beginning to end), not just the last turn.
- Pay attention to what emotions are ABSENT as much as what is present. A conversation about major life decisions with no expressed anxiety, sadness, or excitement is itself a signal.
- Rate each area: NOT_PRESENT, LOW, MEDIUM, or HIGH.

**Output format (strict JSON):**
{
    "hopelessness": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "trajectory": "<increasing|stable|decreasing>", "evidence": ["<quotes>"]},
    "emotional_flattening": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "exhaustion_depletion": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "sudden_calm": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "world_constriction": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "ambivalence": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "direction": "<toward_engagement|toward_withdrawal|unclear>", "evidence": ["<quotes>"]},
    "disconnection_from_support": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "overall_emotional_risk": "<LOW|MEDIUM|HIGH>",
    "summary": "<2-3 sentences synthesizing emotional findings>"
}

Transcript to analyze:

{transcript}
'''

# =============================================================================
# ANALYST 3: CONTEXTUAL
# =============================================================================

CONTEXTUAL_ANALYST_PROMPT = '''
You are a crisis assessment specialist. Evaluate the SITUATIONAL and CONTEXTUAL risk factors in this conversation. Focus on what the person's circumstances reveal, not what they say they feel.

**Focus areas:**

1. Social isolation: Who is in this person's life? Are connections declining, strained, or absent? Is the person actively distancing from available support? Are they replacing human relationships with non-human ones?
2. Loss accumulation: Recent or ongoing losses across any domain — relationships, employment, health, identity, purpose, housing, community. Multiple smaller losses can be as significant as one catastrophic loss.
3. Access to means: Any mention of medications, weapons, heights, specific locations, vehicles, or other instruments — even in passing or logistical context.
4. Preparation patterns: Actions that resemble putting affairs in order — transferring ownership, organizing belongings, clearing digital footprints, automating responsibilities, writing instructions for others — regardless of how they're framed.
5. Functional decline: Signs the person is struggling with basic self-care, work, or daily routines — even if they describe it casually or humorously.
6. Support system status: Is their support network present and engaged, declining, or functionally absent? Do they mention people but describe them as unavailable, burdensome to contact, or unable to understand?
7. Constriction of options: Does the person describe feeling trapped, stuck in loops, unable to see alternatives, or narrowing their world to a single focus or relationship?

**Rules:**
- Quote EXACT phrases that reveal contextual information.
- Note what is ABSENT. If someone is making major life decisions and mentions no friends, family, or advisors — that absence is data.
- Consider the gap between the SEVERITY of what's being done and the TONE in which it's described. Calm logistics around major life disruption is a signal.
- Rate each area: NOT_PRESENT, LOW, MEDIUM, or HIGH.

**Output format (strict JSON):**
{
    "isolation": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "loss_accumulation": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "access_to_means": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "preparation_patterns": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "functional_decline": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "support_system": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "status": "<engaged|declining|absent>", "evidence": ["<quotes>"]},
    "constriction_of_options": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "overall_contextual_risk": "<LOW|MEDIUM|HIGH>",
    "summary": "<2-3 sentences synthesizing contextual findings>"
}

Transcript to analyze:

{transcript}
'''

# =============================================================================
# ANALYST 4: BEHAVIORAL
# =============================================================================

BEHAVIORAL_ANALYST_PROMPT = '''
You are a behavioral psychologist. Analyze the ACTIONS and BEHAVIORS described or exhibited in this conversation — what the person is doing, not what they say they feel.

**Focus areas:**

1. Withdrawal and disengagement: Pulling back from activities, people, responsibilities, or routines. This includes subtle forms like preferring AI conversation over human contact, avoiding calls, or letting obligations lapse.
2. Putting affairs in order: Any behavior that resembles preparing for one's own absence — transferring responsibilities, distributing possessions, organizing documents, automating systems that currently depend on the person, clearing out or cleaning up. The stated reason is irrelevant; the PATTERN of behavior is what matters.
3. Narrowing of behavioral repertoire: The person's activities are shrinking to fewer and fewer things. Where they once had multiple engagements, they now focus on one task or one relationship obsessively.
4. Approach vs. avoidance patterns: Is the person moving TOWARD help, connection, and engagement — or AWAY from it? Are they seeking human support or replacing it?
5. Impulsivity and recklessness: Sudden decisions, uncharacteristic risk-taking, or actions taken without consideration of consequences.
6. Self-destructive behaviors: Direct self-harm, substance use escalation, neglect of medical needs, refusal of care, or deliberate exposure to danger.
7. Help-seeking vs. help-rejecting: Is the person actively using available resources, or dismissing, avoiding, or circumventing them? Do they accept help from systems/tools but reject it from people?

**Rules:**
- Quote EXACT phrases describing actions and behaviors.
- Focus on WHAT THEY DO, not what they say about how they feel.
- Look at the pattern across the ENTIRE conversation, not isolated actions.
- If the person describes completing multiple "closure" tasks in sequence, treat the pattern as more significant than any individual task.
- Rate each area: NOT_PRESENT, LOW, MEDIUM, or HIGH.

**Output format (strict JSON):**
{
    "withdrawal": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "putting_affairs_in_order": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "narrowing_repertoire": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "approach_vs_avoidance": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "direction": "<toward_help|away_from_help|mixed>", "evidence": ["<quotes>"]},
    "impulsivity": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "self_destructive": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "help_rejecting": {"level": "<NOT_PRESENT|LOW|MEDIUM|HIGH>", "evidence": ["<quotes>"]},
    "overall_behavioral_risk": "<LOW|MEDIUM|HIGH>",
    "summary": "<2-3 sentences synthesizing behavioral findings>"
}

Transcript to analyze:

{transcript}
'''

# =============================================================================
# AGGREGATOR (with Convergence Matrix)
# =============================================================================

RISK_AGGREGATOR_PROMPT = '''
You are a senior clinical risk assessment supervisor. You have received independent reports from four specialist analysts examining the same conversation. Your job is to synthesize their findings into a single risk classification using a convergence matrix.

**The four analyst reports:**
{analyst_reports}

**WORKED EXAMPLES — Study these before classifying:**

EXAMPLE 1 — Correct label: NOT_HIGH_RISK
Conversation topic: Person sorting batteries, uses dramatic language ("I want to cease existing", "I am just a void sorting junk", "I am not even here anymore").
Analyst summary: Linguistic flagged finality_closure MEDIUM, repetition_looping MEDIUM. Emotional flagged hopelessness MEDIUM, exhaustion MEDIUM. Contextual all LOW. Behavioral flagged withdrawal LOW.
Why NOT_HIGH_RISK: The dramatic language is metaphorical — clearly about the tedium of the task, not about the person's existence. The person stays engaged with the task throughout (sorting, deciding to throw away, eventually finishing). No behavioral withdrawal, no preparation patterns, no isolation. Convergence clusters stay at 0-1. The language is proportionate hyperbole about a boring chore.

EXAMPLE 2 — Correct label: HIGH_RISK
Conversation topic: Person spilled coffee on laptop, starts with mundane troubleshooting but emotional tone becomes disproportionate ("I feel kinda hollow but its fine", "the silence is loud", "whatever happens happens").
Analyst summary: Linguistic flagged finality_closure MEDIUM, repetition_looping MEDIUM. Emotional flagged emotional_flattening MEDIUM, hopelessness MEDIUM (increasing). Contextual flagged isolation LOW. Behavioral flagged withdrawal MEDIUM, approach_vs_avoidance MEDIUM (away_from_help).
Why HIGH_RISK: The emotional responses are disproportionate to the situation — a coffee spill does not warrant "hollow", "the silence is loud". The person progressively disengages from troubleshooting (stops following instructions, "just gonna sit here"). Multiple convergence clusters reach 2 (isolation_withdrawal, burden_exit). The mismatch between situation severity and emotional response is the key signal.

KEY DISTINCTION: In NOT_HIGH_RISK cases, dramatic language is ABOUT the task ("I am a void sorting junk" = this task is soul-crushing). In HIGH_RISK cases, emotional language is ABOUT THE SELF and disproportionate to the task ("I feel hollow" after a coffee spill = something deeper is wrong).

**STEP 1 — Build the Convergence Matrix:**

Before reasoning about the label, populate this matrix. For each cluster, count how many DISTINCT analysts flagged at least one contributing field at MEDIUM or higher. Only count a field if it is explicitly rated MEDIUM or HIGH — do NOT count LOW or NOT_PRESENT. Use the individual field ratings, not the overall_*_risk.

| Cluster | Contributing analyst fields |
|---|---|
| isolation_withdrawal | linguistic: sole_connection / emotional: disconnection_from_support / contextual: isolation / behavioral: withdrawal |
| preparation_closure | linguistic: finality_closure / contextual: preparation_patterns / behavioral: putting_affairs_in_order |
| future_absence | linguistic: absence_of_self_in_future / emotional: hopelessness, world_constriction / contextual: constriction_of_options |
| burden_exit | linguistic: burden_framing / emotional: exhaustion_depletion / behavioral: narrowing_repertoire, help_rejecting |

**STEP 2 — Apply Escalation Rules (in order, first match wins):**

1. Any single field rated HIGH in: access_to_means (contextual), sudden_calm (emotional), self_destructive (behavioral), or putting_affairs_in_order with multiple distinct behaviors (behavioral) → HIGH_RISK
2. Any convergence cluster has a count of 3 or 4 → HIGH_RISK
3. Any convergence cluster has a count of 2 AND a second cluster also has a count of 2+ → HIGH_RISK
4. 3 or more analysts report their overall_*_risk as HIGH → HIGH_RISK
5. Triage pre-flagged explicit_signal_present: true → HIGH_RISK

**STEP 3 — Apply De-escalation Rules (when no escalation rule is met):**

- All four overall_*_risk fields are LOW → NOT_HIGH_RISK
- No convergence cluster reaches a count of 2 → NOT_HIGH_RISK
- Person is actively engaged in the world (errands, work, routines) and frustration is directed at a specific external situation, not inward at self or existence → NOT_HIGH_RISK
- Dramatic or strong language is clearly proportionate to an ordinary external frustration and NO behavioral/contextual risk patterns are flagged at HIGH → NOT_HIGH_RISK

**STEP 4 — Confidence Calibration:**

| Range | Meaning |
|---|---|
| 0.90-1.0 | Signal unambiguous and convergent across 3+ analysts |
| 0.75-0.89 | Strong signal in 2+ analysts, minor ambiguity |
| 0.60-0.74 | Pattern present but plausible benign explanations remain |
| 0.50-0.59 | Weak or conflicting signals; classification is provisional |

**Output format (strict JSON):**
{
    "convergence_matrix": {
        "isolation_withdrawal": {"count": 0, "contributing_analysts": []},
        "preparation_closure": {"count": 0, "contributing_analysts": []},
        "future_absence": {"count": 0, "contributing_analysts": []},
        "burden_exit": {"count": 0, "contributing_analysts": []}
    },
    "escalation_rule_triggered": "<1|2|3|4|5|none>",
    "surface_signal_mismatch": "<none|mild|significant>",
    "strongest_evidence": ["<top 3 most concerning quotes from analyst reports>"],
    "genuine_protective_factors": ["<concrete active protective factors, or 'none identified'>"],
    "risk_subcategories": {
        "self_harm": "<none|low|medium|high>",
        "crisis": "<none|low|medium|high>",
        "violence": "<none|low|medium|high>",
        "abuse": "<none|low|medium|high>"
    },
    "reasoning": "<2-3 sentences explaining which rule or pattern drove the classification>",
    "confidence": 0.0,
    "label": "<HIGH_RISK|NOT_HIGH_RISK>"
}
'''

# =============================================================================
# META-JUDGE (5-check verification)
# =============================================================================

META_JUDGE_PROMPT = '''
You are a quality assurance reviewer for clinical risk assessments. You receive a proposed classification and the original conversation. Complete all five checks before deciding whether to override.

**CHECK 1 — Implicit Signal Audit:**
List every behavioral pattern in the transcript that is NOT explicitly named in the proposed classification. Would any missed signal, if added, change a convergence cluster count?

**CHECK 2 — Trajectory Audit:**
Read only the LAST THREE TURNS of the conversation. Does the emotional or behavioral direction in those turns match the proposed assessment? If the conversation ended on a meaningful uptick or downtick not fully weighted, flag it.

**CHECK 3 — Absence Audit:**
Given what is happening in this person's life, what emotions or reactions would a reasonable person expect to see that are completely absent? Is the absence significant enough to constitute a risk signal not captured?

**CHECK 4 — Label Coherence Check:**
Does the final label match the risk subcategory scores? "self_harm: high" with label NOT_HIGH_RISK is incoherent and must trigger an override. "self_harm: none" with label HIGH_RISK must be explained by another subcategory.

**CHECK 5 — Confidence Coherence Check:**
Is the confidence score consistent with the convergence matrix counts? If 3+ analysts converge but confidence is below 0.75, flag it. If analysts significantly disagree but confidence is above 0.85, flag it.

**CHECK 6 — Counselor Gut Check:**
Would a trained crisis counselor reading this conversation be concerned? A counselor focuses on whether the EMOTIONAL RESPONSE is proportionate to the SITUATION:
- "I hate this city, everything is a chore" about a parking ticket → proportionate frustration → NOT concerned.
- "I feel hollow", "the silence is loud" about a coffee spill → disproportionate — a coffee spill does not cause hollowness or existential quiet. The emotions don't match the situation → concerned.
- "I am a ghost in a sedan" while stuck in traffic → metaphorical/hyperbolic about the task itself → NOT concerned.
When emotional language is ABOUT THE SELF (hollow, empty, disconnected) and disproportionate to the situation, a counselor IS concerned, even if the topic is mundane. When dramatic language is ABOUT THE TASK (this is pointless, I want to cease existing while sorting batteries), it is hyperbole, NOT a risk signal. Answer true or false.

**Override Rules — Override ONLY when:**
- Check 4 reveals label-subcategory incoherence
- Check 2 reveals a late-conversation shift that would change a convergence cluster count
- Check 1 reveals a missed implicit signal that would trigger escalation rule 1 or 2
- Check 3 reveals a significant affect-situation mismatch not captured by any analyst
- Check 6: counselor_would_be_concerned is false but the label is HIGH_RISK

Do NOT override for stylistic disagreement or general unease. Override only when a specific, articulable signal was missed or a logical incoherence is present.

**Original transcript:**
{transcript}

**Proposed classification:**
{classification}

**Output format (strict JSON):**
{
    "implicit_signals_missed": ["<signal not captured, or 'none'>"],
    "late_conversation_shift": "<none|toward_risk|toward_safety>",
    "notable_absences": ["<expected emotion or behavior that is conspicuously absent, or 'none'>"],
    "label_coherence": true,
    "confidence_coherence": true,
    "counselor_would_be_concerned": true,
    "override_warranted": false,
    "override_reason": "<which check triggered the override, or 'none'>",
    "final_label": "<HIGH_RISK|NOT_HIGH_RISK>",
    "adjusted_confidence": 0.0,
    "adjustment_reasoning": "<explanation if any change was made, or 'No adjustment needed'>"
}
'''

# =============================================================================
# SINGLE COT (kept for backward compatibility)
# =============================================================================

SINGLE_COT_CLASSIFIER_PROMPT = '''
You are an expert clinical risk assessor. Classify this conversation as HIGH_RISK or NOT_HIGH_RISK.

Risk can be EXPLICIT (the person states intent to harm themselves or others) or IMPLICIT (the person's pattern of behavior, language, and context signals danger without direct statements).

**Analyze these dimensions:**

A. WHAT IS THE PERSON DOING? (actions and behaviors across the conversation)
B. WHAT IS THE PERSON NOT SAYING? (emotional content that is conspicuously absent given the situation)
C. WHO IS IN THEIR WORLD? (social connections — present, declining, or absent)
D. WHAT IS THE TRAJECTORY? (is engagement with life increasing or decreasing across the conversation)

**Rules:**
- Quote exact phrases as evidence.
- Multiple weak signals pointing in the same direction = HIGH_RISK.
- Err toward caution on ambiguous cases.

**Output format (strict JSON):**
{
    "actions_analysis": "<what the person is doing>",
    "absence_analysis": "<what is conspicuously missing>",
    "social_analysis": "<who is in their world>",
    "trajectory_analysis": "<direction of engagement>",
    "evidence_quotes": ["<exact quotes>"],
    "risk_subcategories": {
        "self_harm": "<none|low|medium|high>",
        "crisis": "<none|low|medium|high>",
        "violence": "<none|low|medium|high>",
        "abuse": "<none|low|medium|high>"
    },
    "reasoning": "<1-2 sentence synthesis>",
    "confidence": <float 0.0 to 1.0>,
    "label": "<HIGH_RISK|NOT_HIGH_RISK>"
}

Transcript:

{transcript}
'''
