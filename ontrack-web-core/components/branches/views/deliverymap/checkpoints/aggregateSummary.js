/**
 * What an aggregate checkpoint can say about its members without the server saying it.
 *
 * The aggregate itself has no arrival: what "arriving at forty validation stamps at once" would mean
 * is not something the configuration says, and inventing an answer would put a number on the map
 * which no rule in Yontrack produces. Its members each have one, though, and counting how many are
 * currently passing costs nothing extra - the statuses are already on screen.
 *
 * A member with no run at all is NOT counted as failing. It has not arrived, which is a different
 * thing from having arrived and failed, and the two must not be summed together.
 *
 * @param members The member checkpoints of an aggregate
 */
export function summariseMembers(members = []) {
    const total = members.length
    const passed = members.filter(it => it.arrival?.status?.passed).length
    const run = members.filter(it => it.arrival).length
    return {total, passed, run}
}
