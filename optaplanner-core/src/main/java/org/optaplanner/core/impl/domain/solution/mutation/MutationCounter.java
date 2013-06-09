/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.domain.solution.mutation;

import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.impl.domain.entity.PlanningEntityDescriptor;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.solution.Solution;

public class MutationCounter {

    protected final SolutionDescriptor solutionDescriptor;

    public MutationCounter(SolutionDescriptor solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }

    /**
     *
     * @param a never null
     * @param b never null
     * @return >= 0, the number of planning variables that have a different value in {@code a} and {@code b}.
     */
    public int countMutations(Solution a, Solution b) {
        int mutationCount = 0;
        for (PlanningEntityDescriptor entityDescriptor : solutionDescriptor.getEntityDescriptors()) {
            List<Object> aEntities = entityDescriptor.extractEntities(a);
            List<Object> bEntities = entityDescriptor.extractEntities(b);
            for (Iterator aIt = aEntities.iterator(), bIt = bEntities.iterator() ; aIt.hasNext() && bIt.hasNext(); ) {
                Object aEntity =  aIt.next();
                Object bEntity =  bIt.next();
                for (PlanningVariableDescriptor variableDescriptor : entityDescriptor.getVariableDescriptors()) {
                    // TODO broken if the value is an entity, because then it's never the same
                    // But we don't want to depend on value/entity equals()
                    if (variableDescriptor.getValue(aEntity) != variableDescriptor.getValue(bEntity)) {
                        mutationCount++;
                    }
                }
            }
            if (aEntities.size() != bEntities.size()) {
                mutationCount += Math.abs(aEntities.size() - bEntities.size())
                        * entityDescriptor.getVariableDescriptors().size();
            }
        }
        return mutationCount;
    }

}
